#!/bin/bash
# Библиотека функций для управления инстансами сервисов
# Поддерживает динамическое создание инстансов с автоматическим назначением портов

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# === Функции для работы с портами ===

# Получить следующий свободный порт начиная с заданного
get_next_free_port() {
    local start_port=$1
    local port=$start_port
    
    while lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; do
        port=$((port + 1))
    done
    
    echo $port
}

# Вычислить порт для инстанса (базовый_порт + номер_инстанса - 1)
calculate_instance_port() {
    local base_port=$1
    local instance_num=$2
    
    echo $((base_port + instance_num - 1))
}

# === Функции для Service1 (WildFly + Eureka Client) ===

# Запуск одного инстанса Service1 (WildFly)
start_service1_instance() {
    local instance_num=$1
    local wildfly_port=$2
    local eureka_port=$3
    local management_port=$4
    local https_port=$5
    
    local instance_name="service1-instance${instance_num}"
    local wildfly_dir="${PROJECT_DIR}/wildfly-instance${instance_num}"
    local war_path="${PROJECT_DIR}/service1/service1-web/target/service1-web.war"
    
    echo -e "${BLUE}=== Запуск Service1 Instance ${instance_num} ===${NC}"
    echo "  WildFly порт: ${wildfly_port}"
    echo "  Eureka Client порт: ${eureka_port}"
    echo "  Management порт: ${management_port}"
    echo "  HTTPS порт: ${https_port}"
    
    # Проверка что WildFly порт свободен
    if lsof -Pi :$wildfly_port -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${YELLOW}✓ WildFly Instance ${instance_num} уже запущен на порту ${wildfly_port}${NC}"
    else
        # Создаём отдельную директорию WildFly для инстанса (если это не первый)
        if [ $instance_num -eq 1 ]; then
            wildfly_dir="${PROJECT_DIR}/wildfly-33.0.1.Final"
        else
            if [ ! -d "$wildfly_dir" ]; then
                echo "  Создание WildFly instance ${instance_num}..."
                cp -r "${PROJECT_DIR}/wildfly-33.0.1.Final" "$wildfly_dir"
            fi
        fi
        
        # Деплоим WAR
        rm -f $wildfly_dir/standalone/deployments/service1-web.war*
        cp $war_path $wildfly_dir/standalone/deployments/
        
        # Запускаем WildFly с уникальными портами
        nohup $wildfly_dir/bin/standalone.sh \
            -Djboss.node.name=service1-node${instance_num} \
            -Djboss.server.base.dir=$wildfly_dir/standalone \
            -b 0.0.0.0 \
            -Djboss.http.port=$wildfly_port \
            -Djboss.https.port=$https_port \
            -Djboss.management.http.port=$management_port \
            > ${PROJECT_DIR}/logs/${instance_name}.log 2>&1 &
        
        echo $! > ${PROJECT_DIR}/logs/${instance_name}.pid
        
        # Ждём запуска WildFly
        wait_for_service "Service1 WildFly #${instance_num}" $wildfly_port 60
    fi
    
    # Запуск Eureka Client для этого инстанса
    if lsof -Pi :$eureka_port -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${YELLOW}✓ Service1 Eureka Client #${instance_num} уже запущен на порту ${eureka_port}${NC}"
    else
        cd ${PROJECT_DIR}/service1/service1-eureka-client
        
        local wildfly_url="http://localhost:${wildfly_port}/service1-web"
        
        nohup java -jar \
            -Dserver.port=$eureka_port \
            -DWILDFLY_URL=$wildfly_url \
            -Deureka.instance.instance-id=human-beings-service:${eureka_port} \
            target/*.jar \
            > ${PROJECT_DIR}/logs/service1-eureka-client-instance${instance_num}.log 2>&1 &
        
        echo $! > ${PROJECT_DIR}/logs/service1-eureka-client-instance${instance_num}.pid
        cd ${PROJECT_DIR}
        
        wait_for_service "Service1 Eureka Client #${instance_num}" $eureka_port 30
    fi
    
    echo -e "${GREEN}✓ Service1 Instance ${instance_num} запущен${NC}"
    echo "  WildFly:      http://localhost:${wildfly_port}/service1-web/"
    echo "  Eureka Client: http://localhost:${eureka_port}"
    echo ""
}

# === Функции для Service2 (Spring Cloud) ===

# Запуск одного инстанса Service2
start_service2_instance() {
    local instance_num=$1
    local port=$2
    
    local instance_name="service2-instance${instance_num}"
    
    echo -e "${BLUE}=== Запуск Service2 Instance ${instance_num} ===${NC}"
    echo "  Порт: ${port}"
    
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${YELLOW}✓ Service2 Instance ${instance_num} уже запущен на порту ${port}${NC}"
    else
        cd ${PROJECT_DIR}/service2
        
        # Для первого инстанса используем Config Server, для остальных - отключаем
        if [ $instance_num -eq 1 ]; then
            nohup java $JAVA_SSL_OPTS $SERVICE2_MEMORY \
                -Dserver.port=$port \
                -Deureka.instance.instance-id=heroes-service:${port} \
                -jar target/*.jar \
                > ${PROJECT_DIR}/logs/${instance_name}.log 2>&1 &
        else
            nohup java $JAVA_SSL_OPTS $SERVICE2_MEMORY \
                -Dspring.cloud.config.enabled=false \
                -Dserver.port=$port \
                -Deureka.instance.instance-id=heroes-service:${port} \
                -jar target/*.jar \
                > ${PROJECT_DIR}/logs/${instance_name}.log 2>&1 &
        fi
        
        echo $! > ${PROJECT_DIR}/logs/${instance_name}.pid
        cd ${PROJECT_DIR}
        
        wait_for_service "Service2 #${instance_num}" $port 30
    fi
    
    echo -e "${GREEN}✓ Service2 Instance ${instance_num} запущен${NC}"
    echo "  URL: http://localhost:${port}"
    echo ""
}

# === Вспомогательные функции ===

check_port() {
    lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null 2>&1
}

wait_for_service() {
    local name=$1
    local port=$2
    local max=${3:-120}
    
    echo -n "  Ожидание $name"
    for i in $(seq 1 $max); do
        if check_port $port; then
            echo -e " ${GREEN}✓${NC}"
            return 0
        fi
        echo -n "."
        sleep 2
    done
    echo -e " ${RED}✗${NC}"
    return 1
}

# Остановить все инстансы сервиса
stop_service_instances() {
    local service_name=$1  # "service1" или "service2"
    
    echo -e "${YELLOW}Остановка инстансов ${service_name}...${NC}"
    
    # Останавливаем по PID файлам
    for pid_file in ${PROJECT_DIR}/logs/${service_name}-instance*.pid ${PROJECT_DIR}/logs/${service_name}-eureka-client-instance*.pid; do
        if [ -f "$pid_file" ]; then
            pid=$(cat "$pid_file")
            if ps -p $pid > /dev/null 2>&1; then
                echo "  Остановка PID $pid ($(basename $pid_file))"
                kill -9 $pid 2>/dev/null || true
            fi
            rm "$pid_file"
        fi
    done
    
    echo -e "${GREEN}✓ Инстансы ${service_name} остановлены${NC}"
}

# Вывести статус всех инстансов
show_instances_status() {
    echo -e "${BLUE}╔═══════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║           Статус инстансов сервисов                  ║${NC}"
    echo -e "${BLUE}╚═══════════════════════════════════════════════════════╝${NC}"
    echo ""
    
    echo -e "${GREEN}=== Service1 (Human Beings) ===${NC}"
    local s1_count=0
    for i in $(seq 1 10); do
        local port=$(calculate_instance_port $SERVICE1_PORT $i)
        if check_port $port; then
            s1_count=$((s1_count + 1))
            echo -e "  ✓ Instance ${i}: http://localhost:${port}/service1-web/"
            
            local eureka_port=$(calculate_instance_port $SERVICE1_EUREKA_PORT $i)
            if check_port $eureka_port; then
                echo -e "    Eureka Client: http://localhost:${eureka_port}"
            fi
        fi
    done
    
    if [ $s1_count -eq 0 ]; then
        echo -e "  ${RED}Нет запущенных инстансов${NC}"
    else
        echo -e "  Всего запущено: ${GREEN}${s1_count}${NC}"
    fi
    echo ""
    
    echo -e "${GREEN}=== Service2 (Heroes) ===${NC}"
    local s2_count=0
    for i in $(seq 1 10); do
        local port=$(calculate_instance_port $SERVICE2_PORT $i)
        if check_port $port; then
            s2_count=$((s2_count + 1))
            echo -e "  ✓ Instance ${i}: http://localhost:${port}"
        fi
    done
    
    if [ $s2_count -eq 0 ]; then
        echo -e "  ${RED}Нет запущенных инстансов${NC}"
    else
        echo -e "  Всего запущено: ${GREEN}${s2_count}${NC}"
    fi
    echo ""
}

# Экспорт функций для использования в других скриптах
export -f get_next_free_port
export -f calculate_instance_port
export -f start_service1_instance
export -f start_service2_instance
export -f check_port
export -f wait_for_service
export -f stop_service_instances
export -f show_instances_status
