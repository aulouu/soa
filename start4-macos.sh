#!/bin/bash
# Скрипт запуска для Lab4 с Mule ESB

set -e

source config.env
mkdir -p logs

PROJECT_DIR="$(pwd)"
export PROJECT_DIR

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

MODE="${1:---backend}"

# Функция проверки порта
check_port() {
    local port=$1
    nc -z localhost $port 2>/dev/null
    return $?
}

# Функция ожидания запуска сервиса
wait_for_service() {
    local name=$1
    local port=$2
    local max_attempts=30
    local attempt=1
    
    echo -n "Ожидание $name на порту $port..."
    while [ $attempt -le $max_attempts ]; do
        if check_port $port; then
            echo -e " ${GREEN}✓${NC}"
            return 0
        fi
        echo -n "."
        sleep 2
        attempt=$((attempt + 1))
    done
    echo -e " ${RED}✗ Timeout${NC}"
    return 1
}

# Функция остановки
stop_all() {
    echo -e "${RED}🛑 Остановка всех сервисов...${NC}"
    
    # Останавливаем Mule ESB
    if [ -f "logs/mule.pid" ]; then
        pid=$(cat logs/mule.pid)
        if ps -p $pid > /dev/null 2>&1; then
            echo "Stopping Mule ESB (PID $pid)..."
            kill $pid 2>/dev/null || true
        fi
        rm logs/mule.pid
    fi
    
    # Останавливаем REST-adapter
    if [ -f "logs/rest-adapter.pid" ]; then
        pid=$(cat logs/rest-adapter.pid)
        if ps -p $pid > /dev/null 2>&1; then
            echo "Stopping REST-adapter (PID $pid)..."
            kill $pid 2>/dev/null || true
        fi
        rm logs/rest-adapter.pid
    fi
    
    # Останавливаем остальные сервисы
    for pid_file in logs/*.pid; do
        if [ -f "$pid_file" ]; then
            pid=$(cat "$pid_file")
            if ps -p $pid > /dev/null 2>&1; then
                echo "Killing PID $pid from $pid_file..."
                kill -9 $pid 2>/dev/null || true
            fi
            rm "$pid_file"
        fi
    done
    
    pkill -9 -f "service1-web.jar" 2>/dev/null || true
    pkill -9 -f "service2-springcloud" 2>/dev/null || true
    pkill -9 -f "mule" 2>/dev/null || true
    
    echo -e "${GREEN}✓ Все остановлено${NC}"
    exit 0
}

# Обработка флага --stop
if [ "$MODE" == "--stop" ]; then
    stop_all
fi

echo -e "${CYAN}╔═══════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║         Lab4: Запуск с Mule ESB                      ║${NC}"
echo -e "${CYAN}╚═══════════════════════════════════════════════════════╝${NC}"
echo ""

# 1. Consul
echo -e "${BLUE}=== 1/10: Consul ===${NC}"
if ! check_port $CONSUL_PORT; then
    cd scripts && ./start-consul.sh && cd ..
    wait_for_service "Consul" $CONSUL_PORT
else
    echo -e "${YELLOW}✓ Уже запущен${NC}"
fi

# 2. PostgreSQL
echo -e "${BLUE}=== 2/10: PostgreSQL ===${NC}"
if ! pgrep -x postgres > /dev/null; then
    /usr/local/opt/postgresql@14/bin/pg_ctl -D /usr/local/var/postgresql@14 -l logs/postgresql.log start 2>/dev/null || true
    sleep 2
fi
echo -e "${GREEN}✓ Запущен${NC}"

# 3. Config Server
echo -e "${BLUE}=== 3/10: Config Server ===${NC}"
if ! check_port $CONFIG_SERVER_PORT; then
    cd config-server
    nohup java $CONFIG_SERVER_MEMORY -jar target/*.jar > ../logs/config-server.log 2>&1 &
    echo $! > ../logs/config-server.pid
    cd ..
    wait_for_service "Config Server" $CONFIG_SERVER_PORT
else
    echo -e "${YELLOW}✓ Уже запущен${NC}"
fi

# 4. Eureka Server
echo -e "${BLUE}=== 4/10: Eureka Server ===${NC}"
if ! check_port $EUREKA_SERVER_PORT; then
    cd eureka-server
    nohup java $EUREKA_SERVER_MEMORY -jar target/*.jar > ../logs/eureka-server.log 2>&1 &
    echo $! > ../logs/eureka-server.pid
    cd ..
    wait_for_service "Eureka Server" $EUREKA_SERVER_PORT
else
    echo -e "${YELLOW}✓ Уже запущен${NC}"
fi

# 5. Service1 SOAP (WildFly)
echo -e "${BLUE}=== 5/10: Service1 SOAP (WildFly) ===${NC}"
if ! check_port $SERVICE1_PORT; then
    cd wildfly-33.0.1.Final
    cp ../service1/service1-soap/target/service1-soap.war standalone/deployments/
    cp ../service1/service1-ejb/target/service1-ejb-1.0.0.jar standalone/deployments/
    nohup bin/standalone.sh -Djboss.http.port=$SERVICE1_PORT > ../logs/service1-wildfly.log 2>&1 &
    echo $! > ../logs/service1-wildfly.pid
    cd ..
    wait_for_service "Service1 SOAP" $SERVICE1_PORT
else
    echo -e "${YELLOW}✓ Уже запущен${NC}"
fi

# 6. Service1 REST-adapter
echo -e "${BLUE}=== 6/10: Service1 REST-adapter ===${NC}"
if ! check_port $SERVICE1_REST_ADAPTER_PORT; then
    cd service1-rest-adapter
    nohup java -jar target/service1-rest-adapter-1.0.0.jar --server.port=$SERVICE1_REST_ADAPTER_PORT > ../logs/rest-adapter.log 2>&1 &
    echo $! > ../logs/rest-adapter.pid
    cd ..
    wait_for_service "REST-adapter" $SERVICE1_REST_ADAPTER_PORT
else
    echo -e "${YELLOW}✓ Уже запущен${NC}"
fi

# 7. Mule ESB
echo -e "${BLUE}=== 7/10: Mule ESB ===${NC}"
if ! check_port $MULE_ESB_PORT; then
    export JAVA_HOME=$(/usr/libexec/java_home -v 17)
    cd mule-runtime/mule
    nohup bin/mule > ../../logs/mule.log 2>&1 &
    echo $! > ../../logs/mule.pid
    cd ../..
    wait_for_service "Mule ESB" $MULE_ESB_PORT
else
    echo -e "${YELLOW}✓ Уже запущен${NC}"
fi

# 8. Service2 (Heroes)
echo -e "${BLUE}=== 8/10: Service2 (Heroes) ===${NC}"
if ! check_port $SERVICE2_PORT; then
    cd service2
    nohup java $SERVICE2_MEMORY -jar target/service2-1.0.0.jar --server.port=$SERVICE2_PORT > ../logs/service2.log 2>&1 &
    echo $! > ../logs/service2.pid
    cd ..
    wait_for_service "Service2" $SERVICE2_PORT
else
    echo -e "${YELLOW}✓ Уже запущен${NC}"
fi

# 9. Zuul Gateway
echo -e "${BLUE}=== 9/10: Zuul Gateway ===${NC}"
if ! check_port $ZUUL_GATEWAY_PORT; then
    cd zuul-gateway
    nohup java $ZUUL_GATEWAY_MEMORY -jar target/*.jar > ../logs/zuul-gateway.log 2>&1 &
    echo $! > ../logs/zuul-gateway.pid
    cd ..
    wait_for_service "Zuul Gateway" $ZUUL_GATEWAY_PORT
else
    echo -e "${YELLOW}✓ Уже запущен${NC}"
fi

# 10. Frontend (если требуется)
if [ "$MODE" == "--dev" ] || [ "$MODE" == "--build" ]; then
    echo -e "${BLUE}=== 10/10: Frontend ===${NC}"
    cd frontend
    if [ "$MODE" == "--dev" ]; then
        nohup npm start > ../logs/frontend.log 2>&1 &
    else
        npm run build && npx serve -s build -l $FRONTEND_PORT > ../logs/frontend.log 2>&1 &
    fi
    echo $! > ../logs/frontend.pid
    cd ..
    wait_for_service "Frontend" $FRONTEND_PORT
fi

echo ""
echo -e "${GREEN}╔═══════════════════════════════════════════════════════╗${NC}"
echo -e "${GREEN}║             🎉 Все сервисы запущены!                 ║${NC}"
echo -e "${GREEN}╚═══════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${CYAN}📊 Архитектура Lab4:${NC}"
echo -e "  Frontend (:3000) → Zuul Gateway (:8080)"
echo -e "                   ↓"
echo -e "              Service2 (:$SERVICE2_PORT)"
echo -e "                   ↓"
echo -e "           ${YELLOW}Mule ESB (:$MULE_ESB_PORT)${NC}  ← Интеграционная шина"
echo -e "                   ↓"
echo -e "         REST-adapter (:$SERVICE1_REST_ADAPTER_PORT)"
echo -e "                   ↓"
echo -e "           Service1 SOAP (:$SERVICE1_PORT)"
echo -e "                   ↓"
echo -e "              PostgreSQL"
echo ""
echo -e "${CYAN}🔗 URLs:${NC}"
echo -e "  Zuul Gateway:     ${GREEN}http://localhost:$ZUUL_GATEWAY_PORT${NC}"
echo -e "  Eureka Dashboard: ${GREEN}http://localhost:$EUREKA_SERVER_PORT${NC}"
echo -e "  Consul UI:        ${GREEN}http://localhost:$CONSUL_PORT${NC}"
echo -e "  Service1 WSDL:    ${GREEN}http://localhost:$SERVICE1_PORT/service1-soap/HumanBeingService?wsdl${NC}"
echo -e "  Mule ESB:         ${GREEN}http://localhost:$MULE_ESB_PORT/api/human-beings${NC}"
echo ""
echo -e "${YELLOW}⚠️  Для остановки: ./start-lab4.sh --stop${NC}"