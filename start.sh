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
    pkill -9 -f "eureka-server.jar" 2>/dev/null || true
    pkill -9 -f "zuul-gateway.jar" 2>/dev/null || true
    pkill -9 -f "config-server.jar" 2>/dev/null || true
    pkill -9 -f "*:us-cli" 2>/dev/null || true

        
    # На всякий случай общий паттерн для Spring Boot, если имена jar другие
    pkill -9 -f "spring-boot" 2>/dev/null || true

    # 3. Останавливаем Consul
    pkill -9 -f "consul" 2>/dev/null || true

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
    echo "=== 6/7: Service1 (Human Beings) ==="
    if ! check_port $SERVICE1_PORT; then
        # Укажите путь к вашей папке с WildFly
        WILDFLY_HOME="$PROJECT_DIR/wildfly-33.0.1.Final"
        
        # Путь к собранному WAR файлу
        WAR_PATH="service1/service1-web/target/service1-web.war"
        
        # Чистим старые деплои и копируем новый
        rm -f $WILDFLY_HOME/standalone/deployments/service1-web.war*
        cp $WAR_PATH $WILDFLY_HOME/standalone/deployments/

        # Запускаем WildFly
        nohup $WILDFLY_HOME/bin/standalone.sh -b 0.0.0.0 -Djboss.http.port=$SERVICE1_PORT > logs/service1.log 2>&1 &
        
        echo $! > logs/service1.pid
        wait_for_service "Service1" $SERVICE1_PORT
    else
        echo "✓ Уже запущен"
    fi

  

  

    # 7. Service2 (Spring Cloud)
    echo "=== 7/7: Service2 (Heroes) ==="
    if ! check_port $SERVICE2_PORT; then
        cd service2
        nohup java $JAVA_SSL_OPTS $SERVICE2_MEMORY -jar target/*.jar > ../logs/service2.log 2>&1 &
        echo $! > ../logs/service2.pid
        cd ..
        wait_for_service "Service2" $SERVICE2_PORT
    else
        echo "✓ Уже запущен"
    fi

    # 8. Service1 Eureka Client
    echo "=== 8/8: Service1 Eureka Client ==="
    if ! check_port $SERVICE1_EUREKA_PORT; then
        cd service1/service1-eureka-client
        nohup java -jar target/*.jar > ../../logs/service1-eureka-client.log 2>&1 &
        echo $! > ../../logs/service1-eureka-client.pid
        cd ../..
        wait_for_service "Service1 Eureka Client" $SERVICE1_EUREKA_PORT
    else
        echo "✓ Уже запущен"
    fi

    echo ""
    echo "╔═══════════════════════════════════════════════════════╗"
    echo "║              ✓ Все сервисы запущены!                 ║"
    echo "╚═══════════════════════════════════════════════════════╝"
    echo ""
    echo "URLs:"
    echo "  Consul:  http://localhost:$CONSUL_PORT"
    echo "  Eureka:  http://localhost:$EUREKA_SERVER_PORT"
    echo "  Gateway: http://localhost:$ZUUL_GATEWAY_PORT"
    echo ""
    echo "API:"
    echo "  curl http://localhost:$ZUUL_GATEWAY_PORT/api/human-beings"
    echo "  curl http://localhost:$ZUUL_GATEWAY_PORT/api/heroes"
    echo ""
}

start_frontend() {
    echo "=== Frontend ==="
    cd frontend
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
    echo "✓ Frontend: http://localhost:3000"
}

# Обработка аргументов
case "$MODE" in
    --stop)
        stop_all
        ;;
    --dev)
        start_backend
        start_frontend "dev"
        ;;
    --build)
        start_backend
        start_frontend "build"
        ;;
    --backend|*)
        start_backend
        ;;
esac
