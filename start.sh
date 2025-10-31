#!/bin/bash
# Единый скрипт запуска/остановки всего проекта

set -e

source config.env
source ssl-env.sh
mkdir -p logs

PROJECT_DIR="$(pwd)"

MODE="${1:---backend}"

# Функции
check_port() {
    lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null 2>&1
}

wait_for_service() {
    local name=$1 port=$2 max=120
    echo -n "Ожидание $name"
    for i in $(seq 1 $max); do
        check_port $port && { echo " ✓"; return 0; }
        echo -n "."
        sleep 2
    done
    echo " ✗"; return 1
}

stop_all() {
    echo "🛑 Остановка всех сервисов..."

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
    # -9 означает немедленное принудительное завершение
    echo "Cleaning up remaining Java processes..."
    pkill -9 -f "service1-web.jar" 2>/dev/null || true
    pkill -9 -f "service2-1.0.0.jar" 2>/dev/null || true
    pkill -9 -f "service2-springcloud" 2>/dev/null || true
    pkill -9 -f "eureka-server.jar" 2>/dev/null || true
    pkill -9 -f "zuul-gateway.jar" 2>/dev/null || true
    pkill -9 -f "config-server.jar" 2>/dev/null || true
    pkill -9 -f "*:us-cli" 2>/dev/null || true

        
    # На всякий случай общий паттерн для Spring Boot, если имена jar другие
    pkill -9 -f "spring-boot" 2>/dev/null || true

    # 3. Останавливаем Consul
    pkill -9 -f "consul" 2>/dev/null || true

    # 4. Останавливаем Cloudflared
    (cd scripts && ./stop-cloudflared.sh >/dev/null 2>&1 || true)

    # 5. Останавливаем Frontend (npm/node)
    pkill -9 -f "react-scripts" 2>/dev/null || true
    pkill -9 -f "webpack" 2>/dev/null || true

    pkill -9 -f "java" 2>/dev/null || true


    # На случай если он был запущен через скрипт и создал свои лок-файлы
    (cd scripts && ./stop-consul.sh >/dev/null 2>&1 || true)

    echo "✓ Все остановлено и зачищено"
    exit 0
}

start_backend() {
    echo "╔═══════════════════════════════════════════════════════╗"
    echo "║         Запуск микросервисов                          ║"
    echo "╚═══════════════════════════════════════════════════════╝"
    echo ""

    # 1. Consul
    echo "=== 1/7: Consul ==="
    if ! check_port $CONSUL_PORT; then
        cd scripts && ./start-consul.sh && cd ..
        wait_for_service "Consul" $CONSUL_PORT
    else
        echo "✓ Уже запущен"
    fi

    # 2. PostgreSQL
    echo "=== 2/7: PostgreSQL ==="
    if ! pgrep -x postgres > /dev/null; then
        /usr/local/opt/postgresql@14/bin/pg_ctl -D /usr/local/var/postgresql@14 -l logs/postgresql.log start
        sleep 2
    fi
    echo "✓ Запущен"

    # 3. Config Server
    echo "=== 3/7: Config Server ==="
    if ! check_port $CONFIG_SERVER_PORT; then
        cd config-server
        nohup java $JAVA_SSL_OPTS $CONFIG_SERVER_MEMORY -jar target/*.jar > ../logs/config-server.log 2>&1 &
        echo $! > ../logs/config-server.pid
        cd ..
        wait_for_service "Config Server" $CONFIG_SERVER_PORT
    else
        echo "✓ Уже запущен"
    fi

    # 4. Eureka
    echo "=== 4/7: Eureka Server ==="
    if ! check_port $EUREKA_SERVER_PORT; then
        cd eureka-server
        nohup java $JAVA_SSL_OPTS $EUREKA_SERVER_MEMORY -jar target/*.jar > ../logs/eureka-server.log 2>&1 &
        echo $! > ../logs/eureka-server.pid
        cd ..
        wait_for_service "Eureka" $EUREKA_SERVER_PORT
    else
        echo "✓ Уже запущен"
    fi

    # 5. Zuul Gateway
    echo "=== 5/7: Zuul Gateway ==="
    if ! check_port $ZUUL_GATEWAY_PORT; then
        cd zuul-gateway
        nohup java $JAVA_SSL_OPTS $ZUUL_GATEWAY_MEMORY -jar target/*.jar > ../logs/zuul-gateway.log 2>&1 &
        echo $! > ../logs/zuul-gateway.pid
        cd ..
        wait_for_service "Zuul" $ZUUL_GATEWAY_PORT
    else
        echo "✓ Уже запущен"
    fi

        
        
    # 6. Service1 (Jakarta EE + EJB на WildFly)
    echo "=== 6/9: Service1 (Human Beings) Instance 1 ==="
    if ! check_port $SERVICE1_PORT; then
        WILDFLY_HOME="$PROJECT_DIR/wildfly-33.0.1.Final"
        WAR_PATH="service1/service1-web/target/service1-web.war"
        
        rm -f $WILDFLY_HOME/standalone/deployments/service1-web.war*
        cp $WAR_PATH $WILDFLY_HOME/standalone/deployments/

        nohup $WILDFLY_HOME/bin/standalone.sh -b 0.0.0.0 -Djboss.http.port=$SERVICE1_PORT > logs/service1.log 2>&1 &
        
        echo $! > logs/service1.pid
        wait_for_service "Service1" $SERVICE1_PORT
    else
        echo "✓ Уже запущен"
    fi

    # 6.1. Service1 Eureka Client Instance 1
    echo "=== 6.1/9: Service1 Eureka Client Instance 1 ==="
    if ! check_port $SERVICE1_EUREKA_PORT; then
        cd service1/service1-eureka-client
        nohup java -jar target/*.jar > ../../logs/service1-eureka-client.log 2>&1 &
        echo $! > ../../logs/service1-eureka-client.pid
        cd ../..
        wait_for_service "Service1 Eureka Client" $SERVICE1_EUREKA_PORT
    else
        echo "✓ Уже запущен"
    fi

    # 6.2. Service1 Instance 2 (если включено)
    if [ "$SERVICE1_INSTANCES" -ge 2 ]; then
        echo "=== 6.2/9: Service1 Instance 2 ==="
        if ! check_port $SERVICE1_PORT_INSTANCE2; then
            if [ ! -d "wildfly-instance2" ]; then
                echo "Копирование WildFly для инстанса 2..."
                cp -r "$PROJECT_DIR/wildfly-33.0.1.Final" wildfly-instance2
            fi
            
            WAR_PATH="service1/service1-web/target/service1-web.war"
            rm -f wildfly-instance2/standalone/deployments/service1-web.war*
            cp $WAR_PATH wildfly-instance2/standalone/deployments/
            
            nohup wildfly-instance2/bin/standalone.sh \
                -Djboss.node.name=service1-node2 \
                -Djboss.server.base.dir=wildfly-instance2/standalone \
                -b 0.0.0.0 \
                -Djboss.http.port=$SERVICE1_PORT_INSTANCE2 \
                -Djboss.https.port=8444 \
                -Djboss.management.http.port=9991 \
                > logs/service1-instance2.log 2>&1 &
            
            echo $! > logs/service1-instance2.pid
            wait_for_service "Service1 Instance 2" $SERVICE1_PORT_INSTANCE2
        else
            echo "✓ Уже запущен"
        fi
        
        # 6.3. Service1 Eureka Client Instance 2
        echo "=== 6.3/9: Service1 Eureka Client Instance 2 ==="
        if ! check_port $SERVICE1_EUREKA_PORT_INSTANCE2; then
            cd service1/service1-eureka-client
            # Запускаем с указанием на второй WildFly instance
            nohup java -jar \
                -Dserver.port=$SERVICE1_EUREKA_PORT_INSTANCE2 \
                -DWILDFLY_URL=http://localhost:$SERVICE1_PORT_INSTANCE2/service1-web \
                target/*.jar \
                > ../../logs/service1-eureka-client-instance2.log 2>&1 &
            echo $! > ../../logs/service1-eureka-client-instance2.pid
            cd ../..
            wait_for_service "Service1 Eureka Client Instance 2" $SERVICE1_EUREKA_PORT_INSTANCE2
        else
            echo "✓ Уже запущен"
        fi
    fi

    # 7. Service2 (Spring Cloud) Instance 1
    echo "=== 7/9: Service2 (Heroes) Instance 1 ==="
    if ! check_port $SERVICE2_PORT; then
        cd service2
        nohup java $JAVA_SSL_OPTS $SERVICE2_MEMORY -jar target/*.jar > ../logs/service2.log 2>&1 &
        echo $! > ../logs/service2.pid
        cd ..
        wait_for_service "Service2" $SERVICE2_PORT
    else
        echo "✓ Уже запущен"
    fi

    # 7.1. Service2 Instance 2 (если включено)
    if [ "$SERVICE2_INSTANCES" -ge 2 ]; then
        echo "=== 7.1/9: Service2 Instance 2 ==="
        if ! check_port $SERVICE2_PORT_INSTANCE2; then
            cd service2
            # Отключаем Config Server для второго инстанса, чтобы избежать конфликта портов
            nohup java $JAVA_SSL_OPTS $SERVICE2_MEMORY \
                -Dspring.cloud.config.enabled=false \
                -Dserver.port=$SERVICE2_PORT_INSTANCE2 \
                -jar target/*.jar > ../logs/service2-instance2.log 2>&1 &
            echo $! > ../logs/service2-instance2.pid
            cd ..
            wait_for_service "Service2 Instance 2" $SERVICE2_PORT_INSTANCE2
        else
            echo "✓ Уже запущен"
        fi
    fi

    # 8. Cloudflared (если включено)
    if [ "$CLOUDFLARED_ENABLED" = "true" ]; then
        echo "=== 8/9: Cloudflare Tunnel ==="
        cd scripts
        ./start-cloudflared.sh
        cd ..
    fi

    echo ""
    echo "╔═══════════════════════════════════════════════════════╗"
    echo "║              ✓ Все сервисы запущены!                 ║"
    echo "╚═══════════════════════════════════════════════════════╝"
    echo ""
    echo "=== Infrastructure ==="
    echo "  Consul UI:       http://localhost:$CONSUL_PORT"
    echo "  Eureka UI:       http://localhost:$EUREKA_SERVER_PORT"
    echo "  Config Server:   http://localhost:$CONFIG_SERVER_PORT"
    echo ""
    echo "=== API Gateway ==="
    echo "  Zuul Gateway:    https://localhost:$ZUUL_GATEWAY_PORT"
    echo "  Health:          https://localhost:$ZUUL_GATEWAY_PORT/actuator/health"
    echo ""
    echo "=== Services ==="
    echo "  Service1 (Human Beings):"
    echo "    Instance 1:    http://localhost:$SERVICE1_PORT/service1-web/"
    echo "    Eureka Client: http://localhost:$SERVICE1_EUREKA_PORT"
    
    if [ "$SERVICE1_INSTANCES" -ge 2 ]; then
        echo "    Instance 2:    http://localhost:$SERVICE1_PORT_INSTANCE2/service1-web/"
        echo "    Eureka Client: http://localhost:$SERVICE1_EUREKA_PORT_INSTANCE2"
    fi
    
    echo ""
    echo "  Service2 (Heroes):"
    echo "    Instance 1:    http://localhost:$SERVICE2_PORT"
    
    if [ "$SERVICE2_INSTANCES" -ge 2 ]; then
        echo "    Instance 2:    http://localhost:$SERVICE2_PORT_INSTANCE2"
    fi
    
    echo ""
    echo "=== API Examples (через Gateway) ==="
    echo "  Human Beings:    curl -k https://localhost:$ZUUL_GATEWAY_PORT/api/human-beings"
    echo "  Heroes:          curl -k https://localhost:$ZUUL_GATEWAY_PORT/api/heroes"
    
    if [ "$CLOUDFLARED_ENABLED" = "true" ]; then
        if [ -f "cloudflared/tunnel-url-gateway.txt" ]; then
            GATEWAY_URL=$(cat cloudflared/tunnel-url-gateway.txt)
            echo ""
            echo "=== Публичный доступ (Cloudflare Tunnel) ==="
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
    echo "Логи находятся в: ./logs/"
    echo ""
}

start_frontend() {
    echo "=== Frontend ==="
    
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
    echo "✓ Frontend: http://localhost:3000 (API: $API_URL)"
}

# Обработка аргументов
case "$MODE" in
    --stop)
        stop_all
        ;;
    --dev)
        start_backend
        start_frontend "dev"
        echo ""
        echo "╔═══════════════════════════════════════════════════════╗"
        echo "║          ✓ Frontend запущен в dev режиме!            ║"
        echo "╚═══════════════════════════════════════════════════════╝"
        echo ""
        echo "Frontend URL:  http://localhost:3000"
        echo ""
        ;;
    --build)
        start_backend
        start_frontend "build"
        echo ""
        echo "╔═══════════════════════════════════════════════════════╗"
        echo "║       ✓ Frontend запущен (production build)!         ║"
        echo "╚═══════════════════════════════════════════════════════╝"
        echo ""
        echo "Frontend URL:  http://localhost:3000"
        echo ""
        ;;
    --backend|*)
        start_backend
        ;;
esac
