#!/bin/bash
# Единый скрипт запуска/остановки всего проекта с поддержкой динамического масштабирования

set -e

source config.env
source ssl-env.sh
mkdir -p logs

PROJECT_DIR="$(pwd)"
export PROJECT_DIR

# Загружаем библиотеку управления инстансами
source scripts/instance-manager.sh

MODE="${1:---backend}"

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# === Функция остановки ===
stop_all() {
    echo -e "${RED}🛑 Остановка всех сервисов...${NC}"

    # 1. Убиваем по PID файлам (если они есть)
    for pid_file in logs/*.pid; do
        if [ -f "$pid_file" ]; then
            pid=$(cat "$pid_file")
            # Проверяем, существует ли еще процесс
            if ps -p $pid > /dev/null 2>&1; then
                echo "Killing PID $pid from $pid_file..."
                kill -9 $pid 2>/dev/null || true
            fi
            rm "$pid_file"
        fi
    done

    # 2. "Контрольный выстрел" по именам процессов (если PID файлы были потеряны)
    echo "Cleaning up remaining Java processes..."
    pkill -9 -f "service1-web.jar" 2>/dev/null || true
    pkill -9 -f "service2-1.0.0.jar" 2>/dev/null || true
    pkill -9 -f "service2-springcloud" 2>/dev/null || true
    pkill -9 -f "eureka-server.jar" 2>/dev/null || true
    pkill -9 -f "zuul-gateway.jar" 2>/dev/null || true
    pkill -9 -f "config-server.jar" 2>/dev/null || true
    pkill -9 -f "wildfly" 2>/dev/null || true
    pkill -9 -f "spring-boot" 2>/dev/null || true
    pkill -9 -f "consul" 2>/dev/null || true

    # 3. Останавливаем Cloudflared
    (cd scripts && ./stop-cloudflared.sh >/dev/null 2>&1 || true)

    # 4. Останавливаем Frontend (npm/node)
    pkill -9 -f "react-scripts" 2>/dev/null || true
    pkill -9 -f "webpack" 2>/dev/null || true

    # 5. На случай если Consul был запущен через скрипт
    (cd scripts && ./stop-consul.sh >/dev/null 2>&1 || true)

    echo -e "${GREEN}✓ Все остановлено и зачищено${NC}"
    exit 0
}

# === Функция запуска Backend ===
start_backend() {
    echo -e "${CYAN}╔═══════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║         Запуск микросервисов                          ║${NC}"
    echo -e "${CYAN}╚═══════════════════════════════════════════════════════╝${NC}"
    echo ""

    # 1. Consul
    echo -e "${BLUE}=== 1/7: Consul ===${NC}"
    if ! check_port $CONSUL_PORT; then
        cd scripts && ./start-consul.sh && cd ..
        wait_for_service "Consul" $CONSUL_PORT
    else
        echo -e "${YELLOW}✓ Уже запущен${NC}"
    fi

    # 2. PostgreSQL
    echo -e "${BLUE}=== 2/7: PostgreSQL ===${NC}"
    if ! pgrep -x postgres > /dev/null; then
        /usr/local/opt/postgresql@14/bin/pg_ctl -D /usr/local/var/postgresql@14 -l logs/postgresql.log start 2>/dev/null || true
        sleep 2
    fi
    echo -e "${GREEN}✓ Запущен${NC}"

    # 3. Config Server
    echo -e "${BLUE}=== 3/7: Config Server ===${NC}"
    if ! check_port $CONFIG_SERVER_PORT; then
        cd config-server
        nohup java $JAVA_SSL_OPTS $CONFIG_SERVER_MEMORY -jar target/*.jar > ../logs/config-server.log 2>&1 &
        echo $! > ../logs/config-server.pid
        cd ..
        wait_for_service "Config Server" $CONFIG_SERVER_PORT
    else
        echo -e "${YELLOW}✓ Уже запущен${NC}"
    fi

    # 4. Eureka
    echo -e "${BLUE}=== 4/7: Eureka Server ===${NC}"
    if ! check_port $EUREKA_SERVER_PORT; then
        cd eureka-server
        nohup java $JAVA_SSL_OPTS $EUREKA_SERVER_MEMORY -jar target/*.jar > ../logs/eureka-server.log 2>&1 &
        echo $! > ../logs/eureka-server.pid
        cd ..
        wait_for_service "Eureka" $EUREKA_SERVER_PORT
    else
        echo -e "${YELLOW}✓ Уже запущен${NC}"
    fi

    # 5. Zuul Gateway
    echo -e "${BLUE}=== 5/7: Zuul Gateway ===${NC}"
    if ! check_port $ZUUL_GATEWAY_PORT; then
        cd zuul-gateway
        nohup java $JAVA_SSL_OPTS $ZUUL_GATEWAY_MEMORY -jar target/*.jar > ../logs/zuul-gateway.log 2>&1 &
        echo $! > ../logs/zuul-gateway.pid
        cd ..
        wait_for_service "Zuul" $ZUUL_GATEWAY_PORT
    else
        echo -e "${YELLOW}✓ Уже запущен${NC}"
    fi

    echo ""
    echo -e "${CYAN}=== 6/7: Запуск Service1 (${SERVICE1_INSTANCES} инстансов) ===${NC}"
    
    # 6. Service1 - используем новую систему масштабирования
    for i in $(seq 1 $SERVICE1_INSTANCES); do
        local wildfly_port=$(calculate_instance_port $SERVICE1_PORT $i)
        local eureka_port=$(calculate_instance_port $SERVICE1_EUREKA_PORT $i)
        local management_port=$((9990 + $i - 1))
        local https_port=$((8443 + $i - 1))
        
        start_service1_instance $i $wildfly_port $eureka_port $management_port $https_port
    done

    echo ""
    echo -e "${CYAN}=== 7/7: Запуск Service2 (${SERVICE2_INSTANCES} инстансов) ===${NC}"
    
    # 7. Service2 - используем новую систему масштабирования
    for i in $(seq 1 $SERVICE2_INSTANCES); do
        local port=$(calculate_instance_port $SERVICE2_PORT $i)
        start_service2_instance $i $port
    done

    # 8. Cloudflared (если включено)
    if [ "$CLOUDFLARED_ENABLED" = "true" ]; then
        echo -e "${BLUE}=== 8/8: Cloudflare Tunnel ===${NC}"
        cd scripts
        ./start-cloudflared.sh
        cd ..
    fi

    echo ""
    echo -e "${GREEN}╔═══════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║              ✓ Все сервисы запущены!                 ║${NC}"
    echo -e "${GREEN}╚═══════════════════════════════════════════════════════╝${NC}"
    echo ""
    
    print_service_info
}

# === Функция вывода информации о сервисах ===
print_service_info() {
    echo -e "${BLUE}=== Infrastructure ===${NC}"
    echo "  Consul UI:       http://localhost:$CONSUL_PORT"
    echo "  Eureka UI:       http://localhost:$EUREKA_SERVER_PORT"
    echo "  Config Server:   http://localhost:$CONFIG_SERVER_PORT"
    echo ""
    echo -e "${BLUE}=== API Gateway ===${NC}"
    echo "  Zuul Gateway:    https://localhost:$ZUUL_GATEWAY_PORT"
    echo "  Health:          https://localhost:$ZUUL_GATEWAY_PORT/actuator/health"
    echo ""
    echo -e "${BLUE}=== Services ===${NC}"
    echo -e "${GREEN}Service1 (Human Beings) - ${SERVICE1_INSTANCES} инстансов:${NC}"
    
    for i in $(seq 1 $SERVICE1_INSTANCES); do
        local wildfly_port=$(calculate_instance_port $SERVICE1_PORT $i)
        local eureka_port=$(calculate_instance_port $SERVICE1_EUREKA_PORT $i)
        echo "  Instance $i:"
        echo "    WildFly:       http://localhost:${wildfly_port}/service1-web/"
        echo "    Eureka Client: http://localhost:${eureka_port}"
    done
    
    echo ""
    echo -e "${GREEN}Service2 (Heroes) - ${SERVICE2_INSTANCES} инстансов:${NC}"
    
    for i in $(seq 1 $SERVICE2_INSTANCES); do
        local port=$(calculate_instance_port $SERVICE2_PORT $i)
        echo "  Instance $i:     http://localhost:${port}"
    done
    
    echo ""
    echo -e "${BLUE}=== API Examples (через Gateway) ===${NC}"
    echo "  Human Beings:    curl -k https://localhost:$ZUUL_GATEWAY_PORT/api/human-beings"
    echo "  Heroes:          curl -k https://localhost:$ZUUL_GATEWAY_PORT/api/heroes"
    
    if [ "$CLOUDFLARED_ENABLED" = "true" ]; then
        if [ -f "cloudflared/tunnel-url-gateway.txt" ]; then
            GATEWAY_URL=$(cat cloudflared/tunnel-url-gateway.txt)
            echo ""
            echo -e "${CYAN}=== Публичный доступ (Cloudflare Tunnel) ===${NC}"
            echo "  Gateway URL:     $GATEWAY_URL"
            echo "  Human Beings:    curl $GATEWAY_URL/api/human-beings"
            echo "  Heroes:          curl $GATEWAY_URL/api/heroes"
            
            if [ -f "cloudflared/tunnel-url-frontend.txt" ]; then
                FRONTEND_URL=$(cat cloudflared/tunnel-url-frontend.txt)
                echo "  Frontend URL:    $FRONTEND_URL"
            fi
        fi
    fi
    
    echo ""
    echo -e "${YELLOW}💡 Для управления масштабированием используйте: ./scale.sh${NC}"
    echo "   Примеры:"
    echo "     ./scale.sh status           # Посмотреть статус"
    echo "     ./scale.sh service1 5       # Запустить 5 инстансов Service1"
    echo "     ./scale.sh service2 3       # Запустить 3 инстанса Service2"
    echo ""
    echo "Логи находятся в: ./logs/"
    echo ""
}

# === Функция запуска Frontend ===
start_frontend() {
    echo -e "${BLUE}=== Frontend ===${NC}"
    
    # Определяем API URL для фронтенда
    API_URL="https://localhost:$ZUUL_GATEWAY_PORT"
    
    # Если Cloudflare включен и туннель запущен, используем публичный URL
    if [ "$CLOUDFLARED_ENABLED" = "true" ] && [ -f "cloudflared/tunnel-url-gateway.txt" ]; then
        GATEWAY_URL=$(cat cloudflared/tunnel-url-gateway.txt)
        if [ -n "$GATEWAY_URL" ]; then
            API_URL="$GATEWAY_URL"
            echo "Используем Cloudflare Tunnel для API: $API_URL"
        fi
    fi
    
    cd frontend
    
    # Создаём .env файл для фронтенда с правильным API URL
    cat > .env.local <<EOF
# Автоматически сгенерировано start.sh
REACT_APP_API_URL=$API_URL
EOF
    
    if [ "$1" = "build" ]; then
        echo "Сборка production..."
        npm run build
        npx serve -s build -l 3000 &
        echo $! > ../logs/frontend.pid
    else
        echo "Запуск dev режима..."
        npm start &
        echo $! > ../logs/frontend.pid
    fi
    cd ..
    echo -e "${GREEN}✓ Frontend: http://localhost:3000 (API: $API_URL)${NC}"
}

# === Обработка аргументов ===
case "$MODE" in
    --stop)
        stop_all
        ;;
    --dev)
        start_backend
        start_frontend "dev"
        echo ""
        echo -e "${GREEN}╔═══════════════════════════════════════════════════════╗${NC}"
        echo -e "${GREEN}║          ✓ Frontend запущен в dev режиме!            ║${NC}"
        echo -e "${GREEN}╚═══════════════════════════════════════════════════════╝${NC}"
        echo ""
        echo "Frontend URL:  http://localhost:3000"
        echo ""
        ;;
    --build)
        start_backend
        start_frontend "build"
        echo ""
        echo -e "${GREEN}╔═══════════════════════════════════════════════════════╗${NC}"
        echo -e "${GREEN}║       ✓ Frontend запущен (production build)!         ║${NC}"
        echo -e "${GREEN}╚═══════════════════════════════════════════════════════╝${NC}"
        echo ""
        echo "Frontend URL:  http://localhost:3000"
        echo ""
        ;;
    --backend|*)
        start_backend
        ;;
esac
