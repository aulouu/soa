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
    (echo > /dev/tcp/127.0.0.1/$port) >/dev/null 2>&1
    return $?
}

## Функция для принудительной очистки портов
#cleanup_ports() {
#    echo -e "${YELLOW}🧹 Очистка занятых портов...${NC}"
#
#    # Список портов для очистки
#    local ports_to_clean=(8080 8082 8084 8091 8081 9090 8888 8761 8500 9990 9991 8443 8445)
#
#    for port in "${ports_to_clean[@]}"; do
#        if check_port $port; then
#            echo "Порт $port занят, поиск процесса..."
#
#            # Для Windows используем netstat для поиска PID
#            local pid=$(netstat -ano 2>/dev/null | grep ":$port" | awk '{print $5}' | head -1)
#
#            # Альтернативный способ для Windows
#            if [ -z "$pid" ]; then
#                pid=$(netstat -ano 2>/dev/null | findstr ":$port" | awk '{print $5}' | head -1)
#            fi
#
#            if [ ! -z "$pid" ]; then
#                echo "Убиваем процесс $pid на порту $port"
#                taskkill /PID $pid /F 2>/dev/null || kill -9 $pid 2>/dev/null || true
#            else
#                echo "Не удалось найти PID для порта $port, пробуем по имени..."
#                # Убиваем процессы по имени для известных портов
#                case $port in
#                    8080)
#                        pkill -9 -f "zuul-gateway" 2>/dev/null || true
#                        pkill -9 -f "gateway" 2>/dev/null || true
#                        ;;
#                    8081|9090)
#                        pkill -9 -f "mule" 2>/dev/null || true
#                        ;;
#                    8091)
#                        pkill -9 -f "service2" 2>/dev/null || true
#                        pkill -9 -f "springcloud" 2>/dev/null || true
#                        ;;
#                    8082|8084)
#                        pkill -9 -f "wildfly" 2>/dev/null || true
#                        pkill -9 -f "jboss" 2>/dev/null || true
#                        pkill -9 -f "standalone.sh" 2>/dev/null || true
#                        ;;
#                esac
#            fi
#        fi
#    done
#
#    # Дополнительно: убиваем все Java процессы для Zuul и Gateway
#    echo "Остановка Zuul/Gateway процессов..."
#    pkill -9 -f "zuul" 2>/dev/null || true
#    pkill -9 -f "gateway" 2>/dev/null || true
#
#    # Ждем освобождения портов
#    sleep 3
#
#    echo -e "${GREEN}✓ Очистка портов завершена${NC}"
#}

# Функция для принудительной очистки портов Windows
cleanup_ports() {
    echo -e "${YELLOW}🧹 Очистка занятых портов...${NC}"

    # Список портов для очистки
    local ports_to_clean=(8080 8082 8084 8091 8081 9090 8888 8761 8500 9990 9991 8443 8445)

    for port in "${ports_to_clean[@]}"; do
        echo "Проверка порта $port..."

        # Используем netstat для поиска процессов на порту
        local pids=$(netstat -ano 2>/dev/null | findstr ":$port " | awk '{print $5}' | sort -u)

        if [ ! -z "$pids" ]; then
            echo "Найдены процессы на порту $port: $pids"
            for pid in $pids; do
                echo "Убиваем процесс $pid..."
                taskkill /F /PID $pid 2>/dev/null || true
                # Альтернативный способ для Windows
                wmic process where "ProcessId=$pid" delete 2>/dev/null || true
            done
        fi

        # Дополнительная очистка по имени процесса для ключевых портов
        case $port in
            8082)  # WildFly порт
                echo "Принудительная очистка WildFly процессов..."
                # Убиваем все Java процессы связанные с WildFly
                taskkill /F /IM java.exe /FI "WINDOWTITLE eq WildFly*" 2>/dev/null || true
                taskkill /F /IM javaw.exe /FI "WINDOWTITLE eq WildFly*" 2>/dev/null || true
                # Ищем процессы в папке wildfly
                wmic process where "CommandLine like '%wildfly%'" delete 2>/dev/null || true
                wmic process where "CommandLine like '%jboss%'" delete 2>/dev/null || true
                ;;
        esac

        sleep 1
    done

    echo -e "${GREEN}✓ Очистка портов завершена${NC}"
    sleep 2
}

# Функция остановки
stop_all() {
    echo -e "${RED}🛑 Остановка всех сервисов...${NC}"

    # Сначала остановка через управление сервисами
    echo "Остановка через управление сервисами..."

    # Останавливаем WildFly через jboss-cli если доступен
    if [ -f "wildfly-33.0.1.Final/bin/jboss-cli.bat" ]; then
        echo "Остановка WildFly через jboss-cli..."
        cd wildfly-33.0.1.Final
        cmd.exe /c "bin\jboss-cli.bat --connect command=:shutdown" 2>/dev/null || true
        cd ..
        sleep 5
    fi

    # Принудительная очистка портов
    cleanup_ports

    # Удаляем PID файлы
    echo "Очистка PID файлов..."
    rm -f logs/*.pid

    echo -e "${GREEN}✓ Все остановлено${NC}"
    exit 0
}

# Функция сборки Maven проекта
build_maven_project() {
    local project_dir=$1
    local project_name=$2

    echo -e "${YELLOW}📦 Сборка $project_name...${NC}"

    # Создаем папку logs если её нет
    mkdir -p "$PROJECT_DIR/logs"

    # Переходим в директорию проекта
    cd "$project_dir"

    if [ -f "pom.xml" ]; then
        # Используем абсолютный путь для логов
        if mvn clean package -DskipTests 2>&1 | tee "$PROJECT_DIR/logs/build-$(basename $project_dir).log"; then
            echo -e "${GREEN}✓ $project_name собран успешно${NC}"
            cd "$PROJECT_DIR"
            return 0
        else
            echo -e "${RED}✗ Ошибка сборки $project_name${NC}"
            cd "$PROJECT_DIR"
            return 1
        fi
    else
        echo -e "${YELLOW}⚠️  pom.xml не найден в $project_dir${NC}"
        cd "$PROJECT_DIR"
        return 1
    fi
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
#stop_all() {
#    echo -e "${RED}🛑 Остановка всех сервисов...${NC}"
#
#    # Останавливаем WildFly (Service1 SOAP) - специальная обработка
#    if [ -f "logs/service1-wildfly.pid" ]; then
#        pid=$(cat logs/service1-wildfly.pid)
#        if ps -p $pid > /dev/null 2>&1; then
#            echo "Stopping WildFly (Service1 SOAP) (PID $pid)..."
#            kill -15 $pid 2>/dev/null || true
#            sleep 2
#            # Если не остановился, принудительно
#            if ps -p $pid > /dev/null 2>&1; then
#                kill -9 $pid 2>/dev/null || true
#            fi
#        fi
#        rm logs/service1-wildfly.pid
#    fi
#    # Дополнительно убиваем все процессы WildFly
#    pkill -9 -f "standalone.sh" 2>/dev/null || true
#    pkill -9 -f "jboss-modules.jar" 2>/dev/null || true
#
#    # Останавливаем Mule ESB
#    if [ -f "logs/mule.pid" ]; then
#        pid=$(cat logs/mule.pid)
#        if ps -p $pid > /dev/null 2>&1; then
#            echo "Stopping Mule ESB (PID $pid)..."
#            kill $pid 2>/dev/null || true
#        fi
#        rm logs/mule.pid
#    fi
#
#    # Останавливаем REST-adapter
#    if [ -f "logs/rest-adapter.pid" ]; then
#        pid=$(cat logs/rest-adapter.pid)
#        if ps -p $pid > /dev/null 2>&1; then
#            echo "Stopping REST-adapter (PID $pid)..."
#            kill $pid 2>/dev/null || true
#        fi
#        rm logs/rest-adapter.pid
#    fi
#
#    # Останавливаем остальные сервисы
#    for pid_file in logs/*.pid; do
#        if [ -f "$pid_file" ]; then
#            pid=$(cat "$pid_file")
#            if ps -p $pid > /dev/null 2>&1; then
#                echo "Killing PID $pid from $pid_file..."
#                kill -9 $pid 2>/dev/null || true
#            fi
#            rm "$pid_file"
#        fi
#    done
#
#    # Убиваем процессы по имени
#    pkill -9 -f "service1-web.jar" 2>/dev/null || true
#    pkill -9 -f "service2-springcloud" 2>/dev/null || true
#    pkill -9 -f "mule" 2>/dev/null || true
#    pkill -9 -f "standalone.sh" 2>/dev/null || true
#
#    echo -e "${GREEN}✓ Все остановлено${NC}"
#    exit 0
#}

# Обработка флага --stop
if [ "$MODE" == "--stop" ]; then
    stop_all
fi

echo -e "${CYAN}╔═══════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║         Lab4: Запуск с Mule ESB                       ║${NC}"
echo -e "${CYAN}╚═══════════════════════════════════════════════════════╝${NC}"
echo ""

## Фаза сборки всех проектов
echo -e "${BLUE}=== Фаза сборки ===${NC}"

# 1. Config Server
build_maven_project "config-server" "Config Server"

# 2. Eureka Server
build_maven_project "eureka-server" "Eureka Server"

# 3. Service1 - EJB модуль
build_maven_project "service1/service1-ejb" "Service1 EJB"

# 4. Service1 - SOAP Web Service
build_maven_project "service1/service1-soap" "Service1 SOAP"

# 5. Service1 REST-adapter
build_maven_project "service1-rest-adapter" "Service1 REST-adapter"

# 6. Service2
build_maven_project "service2" "Service2"

# 7. Zuul Gateway
build_maven_project "zuul-gateway" "Zuul Gateway"

# 8. Mule Integration App (если есть)
if [ -d "mule-integration-app" ]; then
    build_maven_project "mule-integration-app" "Mule Integration App"

    # Копируем JAR в папку apps Mule Runtime
    echo -e "${YELLOW}📦 Копирование Mule приложения в runtime...${NC}"
    cp mule-integration-app/target/mule-integration-app-*.jar \
       mule-runtime/mule-standalone-4.4.0/apps/
    echo -e "${GREEN}✓ Mule приложение скопировано в apps/${NC}"
fi

# Фаза запуска сервисов

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
echo -e "${BLUE}=== 5/10: Service1 SOAP \(WildFly\) ===${NC}"

# Принудительно убиваем все процессы WildFly
echo "Остановка всех WildFly процессов..."
pkill -9 -f "standalone.sh" 2>/dev/null || true
pkill -9 -f "jboss-modules.jar" 2>/dev/null || true

# Удаляем старые PID файлы
rm -f logs/service1-wildfly.pid

# Ждем освобождения портов
sleep 3

# Проверяем свободен ли порт
if check_port $SERVICE1_PORT; then
    echo -e "${RED}✗ Порт $SERVICE1_PORT все еще занят!${NC}"
    echo "Поиск процессов на порту $SERVICE1_PORT..."
    netstat -ano | findstr :$SERVICE1_PORT
    read -p "Нажмите Enter чтобы продолжить или Ctrl+C для отмены..."
fi

# Запускаем WildFly
cd wildfly-33.0.1.Final

# Удаляем старые deployments
rm -f standalone/deployments/service1-*.war
rm -f standalone/deployments/service1-*.jar

# Копируем новые артефакты
cp ../service1/service1-soap/target/service1-soap.war standalone/deployments/
cp ../service1/service1-ejb/target/service1-ejb-1.0.0.jar standalone/deployments/

echo "Запуск WildFly на порту $SERVICE1_PORT..."
nohup bin/standalone.sh -Djboss.http.port=$SERVICE1_PORT > ../logs/service1-wildfly.log 2>&1 &
WILDFLY_PID=$!
echo $WILDFLY_PID > ../logs/service1-wildfly.pid

cd ..

# Ждем запуска с проверкой логов
echo -n "Ожидание запуска WildFly..."
ATTEMPT=1
MAX_ATTEMPTS=45

while [ $ATTEMPT -le $MAX_ATTEMPTS ]; do
    if check_port $SERVICE1_PORT; then
        # Проверяем что процесс все еще жив
        if ps -p $WILDFLY_PID > /dev/null 2>&1; then
            echo -e " ${GREEN}✓ Запущен${NC}"
            break
        else
            echo -e " ${RED}✗ Процесс умер${NC}"
            tail -5 logs/service1-wildfly.log
            exit 1
        fi
    fi

    # Проверяем логи на наличие ошибок
    if tail -1 logs/service1-wildfly.log 2>/dev/null | grep -q "WFLYSRV0026.*started.*with errors"; then
        echo -e " ${RED}✗ WildFly запустился с ошибками${NC}"
        tail -20 logs/service1-wildfly.log
        exit 1
    fi

    echo -n "."
    sleep 2
    ATTEMPT=$((ATTEMPT + 1))
done

if [ $ATTEMPT -gt $MAX_ATTEMPTS ]; then
    echo -e " ${RED}✗ Timeout${NC}"
    tail -20 logs/service1-wildfly.log
    exit 1
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

# 7. Mule ESB (Community Edition)
echo -e "${BLUE}=== 7/10: Mule ESB ===${NC}"
if ! check_port $MULE_ESB_PORT; then
    export JAVA_HOME="/c/Program Files/Java/jre1.8.0_471"
    export PATH="$JAVA_HOME/bin:$PATH"
    cd mule-runtime/mule-standalone-4.4.0
    cmd.exe /c "bin\mule.bat" > ../../logs/mule.log 2>&1 &
    echo $! > ../../logs/mule.pid
    cd ../..
    wait_for_service "Mule ESB" $MULE_ESB_PORT
else
    echo -e "${YELLOW}✓ Уже запущен${NC}"
fi

# 8. Service2 (Heroes)
echo -e "${BLUE}=== 8/10: Service2 (Heroes) ===${NC}"
if ! check_port $SERVICE2_PORT; then
    export JAVA_HOME="/c/Program Files/Java/jdk-17.0.4"
    export PATH="$JAVA_HOME/bin:$PATH"
    cd service2
    nohup java $SERVICE2_MEMORY -jar target/service2-springcloud-1.0.0.jar --server.port=$SERVICE2_PORT > ../logs/service2.log 2>&1 &
    echo $! > ../logs/service2.pid
    cd ..
    wait_for_service "Service2" $SERVICE2_PORT
else
    echo -e "${YELLOW}✓ Уже запущен${NC}"
fi

# 9. Zuul Gateway
echo -e "${BLUE}=== 9/10: Zuul Gateway ===${NC}"
if ! check_port $ZUUL_GATEWAY_PORT; then
    export JAVA_HOME="/c/Program Files/Java/jdk-17.0.4"
    export PATH="$JAVA_HOME/bin:$PATH"
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

    # Сборка фронтенда если требуется
    if [ "$MODE" == "--build" ]; then
        echo -e "${YELLOW}📦 Сборка фронтенда...${NC}"
        npm run build
    fi

    if [ "$MODE" == "--dev" ]; then
        nohup npm start > ../logs/frontend.log 2>&1 &
    else
        npx serve -s build -l $FRONTEND_PORT > ../logs/frontend.log 2>&1 &
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
