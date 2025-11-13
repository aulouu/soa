#!/bin/bash
# Утилита для управления масштабированием сервисов
# Позволяет легко запускать/останавливать инстансы без редактирования config.env

set -e

# Определяем директорию проекта
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$PROJECT_DIR"

# Загружаем конфигурацию
source config.env
source ssl-env.sh
source scripts/instance-manager.sh

# Цвета
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

# === Показать помощь ===
show_help() {
    echo -e "${CYAN}╔═══════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║          Утилита масштабирования сервисов             ║${NC}"
    echo -e "${CYAN}╚═══════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo "Использование:"
    echo "  ./scale.sh [команда] [параметры]"
    echo ""
    echo "Команды:"
    echo -e "  ${GREEN}status${NC}"
    echo "      Показать текущее состояние всех инстансов"
    echo ""
    echo -e "  ${GREEN}service1 <count>${NC}"
    echo "      Запустить N инстансов Service1 (Human Beings)"
    echo "      Пример: ./scale.sh service1 3"
    echo ""
    echo -e "  ${GREEN}service2 <count>${NC}"
    echo "      Запустить N инстансов Service2 (Heroes)"
    echo "      Пример: ./scale.sh service2 2"
    echo ""
    echo -e "  ${GREEN}all <service1_count> <service2_count>${NC}"
    echo "      Запустить N инстансов обоих сервисов"
    echo "      Пример: ./scale.sh all 3 2"
    echo ""
    echo -e "  ${GREEN}stop-service1${NC}"
    echo "      Остановить все инстансы Service1"
    echo ""
    echo -e "  ${GREEN}stop-service2${NC}"
    echo "      Остановить все инстансы Service2"
    echo ""
    echo -e "  ${GREEN}restart-service1 <count>${NC}"
    echo "      Перезапустить Service1 с новым количеством инстансов"
    echo ""
    echo -e "  ${GREEN}restart-service2 <count>${NC}"
    echo "      Перезапустить Service2 с новым количеством инстансов"
    echo ""
    echo "Примечания:"
    echo "  - Для Service1 требуется инфраструктура (Consul, Eureka, и т.д.)"
    echo "  - Порты назначаются автоматически (SERVICE1_PORT + номер - 1)"
    echo "  - Service1: порт WildFly + порт Eureka Client + management порт"
    echo "  - Service2: только один порт"
    echo ""
    echo "Примеры использования:"
    echo "  ./scale.sh status                  # Посмотреть текущее состояние"
    echo "  ./scale.sh service1 3              # Запустить 3 инстанса Service1"
    echo "  ./scale.sh service2 5              # Запустить 5 инстансов Service2"
    echo "  ./scale.sh all 3 4                 # 3 инстанса Service1 + 4 Service2"
    echo "  ./scale.sh restart-service1 2      # Перезапустить Service1 (2 инстанса)"
    echo ""
}

# === Проверка инфраструктуры ===
check_infrastructure() {
    local missing=0
    
    echo -e "${BLUE}Проверка инфраструктуры...${NC}"
    
    # Consul
    if ! check_port $CONSUL_PORT; then
        echo -e "${RED}✗ Consul не запущен (порт $CONSUL_PORT)${NC}"
        missing=1
    else
        echo -e "${GREEN}✓ Consul${NC}"
    fi
    
    # Eureka
    if ! check_port $EUREKA_SERVER_PORT; then
        echo -e "${RED}✗ Eureka не запущен (порт $EUREKA_SERVER_PORT)${NC}"
        missing=1
    else
        echo -e "${GREEN}✓ Eureka${NC}"
    fi
    
    # Config Server
    if ! check_port $CONFIG_SERVER_PORT; then
        echo -e "${RED}✗ Config Server не запущен (порт $CONFIG_SERVER_PORT)${NC}"
        missing=1
    else
        echo -e "${GREEN}✓ Config Server${NC}"
    fi
    
    # Zuul Gateway
    if ! check_port $ZUUL_GATEWAY_PORT; then
        echo -e "${RED}✗ Zuul Gateway не запущен (порт $ZUUL_GATEWAY_PORT)${NC}"
        missing=1
    else
        echo -e "${GREEN}✓ Zuul Gateway${NC}"
    fi
    
    echo ""
    
    if [ $missing -eq 1 ]; then
        echo -e "${YELLOW}⚠ Некоторые компоненты инфраструктуры не запущены${NC}"
        echo -e "${YELLOW}  Запустите их командой: ./start.sh --backend${NC}"
        echo ""
        read -p "Продолжить всё равно? (y/N): " choice
        if [[ ! "$choice" =~ ^[Yy]$ ]]; then
            exit 1
        fi
    fi
}

# === Масштабирование Service1 ===
scale_service1() {
    local target_count=$1
    
    if [ -z "$target_count" ] || ! [[ "$target_count" =~ ^[0-9]+$ ]]; then
        echo -e "${RED}Ошибка: Укажите корректное количество инстансов${NC}"
        exit 1
    fi
    
    if [ $target_count -lt 1 ]; then
        echo -e "${RED}Ошибка: Минимум 1 инстанс${NC}"
        exit 1
    fi
    
    if [ $target_count -gt 10 ]; then
        echo -e "${RED}Ошибка: Максимум 10 инстансов (можно увеличить в скрипте)${NC}"
        exit 1
    fi
    
    echo -e "${CYAN}╔═══════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║    Масштабирование Service1 до ${target_count} инстансов${NC}"
    echo -e "${CYAN}╚═══════════════════════════════════════════════════════╝${NC}"
    echo ""
    
    check_infrastructure
    
    # Запускаем нужное количество инстансов
    for i in $(seq 1 $target_count); do
        local wildfly_port=$(calculate_instance_port $SERVICE1_PORT $i)
        local eureka_port=$(calculate_instance_port $SERVICE1_EUREKA_PORT $i)
        local management_port=$((9990 + $i - 1))
        local https_port=$((8443 + $i - 1))
        
        start_service1_instance $i $wildfly_port $eureka_port $management_port $https_port
    done
    
    echo -e "${GREEN}╔═══════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║       ✓ Service1 масштабирован успешно!              ║${NC}"
    echo -e "${GREEN}╚═══════════════════════════════════════════════════════╝${NC}"
    echo ""
    
    show_instances_status
}

# === Масштабирование Service2 ===
scale_service2() {
    local target_count=$1
    
    if [ -z "$target_count" ] || ! [[ "$target_count" =~ ^[0-9]+$ ]]; then
        echo -e "${RED}Ошибка: Укажите корректное количество инстансов${NC}"
        exit 1
    fi
    
    if [ $target_count -lt 1 ]; then
        echo -e "${RED}Ошибка: Минимум 1 инстанс${NC}"
        exit 1
    fi
    
    if [ $target_count -gt 10 ]; then
        echo -e "${RED}Ошибка: Максимум 10 инстансов (можно увеличить в скрипте)${NC}"
        exit 1
    fi
    
    echo -e "${CYAN}╔═══════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║    Масштабирование Service2 до ${target_count} инстансов${NC}"
    echo -e "${CYAN}╚═══════════════════════════════════════════════════════╝${NC}"
    echo ""
    
    check_infrastructure
    
    # Запускаем нужное количество инстансов
    for i in $(seq 1 $target_count); do
        local port=$(calculate_instance_port $SERVICE2_PORT $i)
        start_service2_instance $i $port
    done
    
    echo -e "${GREEN}╔═══════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║       ✓ Service2 масштабирован успешно!              ║${NC}"
    echo -e "${GREEN}╚═══════════════════════════════════════════════════════╝${NC}"
    echo ""
    
    show_instances_status
}

# === Масштабирование обоих сервисов ===
scale_all() {
    local service1_count=$1
    local service2_count=$2
    
    if [ -z "$service1_count" ] || [ -z "$service2_count" ]; then
        echo -e "${RED}Ошибка: Укажите количество инстансов для обоих сервисов${NC}"
        echo "Пример: ./scale.sh all 3 2"
        exit 1
    fi
    
    echo -e "${CYAN}╔═══════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║          Масштабирование всех сервисов                ║${NC}"
    echo -e "${CYAN}║  Service1: ${service1_count} | Service2: ${service2_count}${NC}"
    echo -e "${CYAN}╚═══════════════════════════════════════════════════════╝${NC}"
    echo ""
    
    check_infrastructure
    
    scale_service1 $service1_count
    echo ""
    scale_service2 $service2_count
}

# === Перезапуск сервисов ===
restart_service1() {
    local count=$1
    
    if [ -z "$count" ]; then
        echo -e "${RED}Ошибка: Укажите количество инстансов${NC}"
        exit 1
    fi
    
    echo -e "${YELLOW}Остановка Service1...${NC}"
    stop_service_instances "service1"
    
    echo ""
    echo -e "${GREEN}Запуск Service1 (${count} инстансов)...${NC}"
    scale_service1 $count
}

restart_service2() {
    local count=$1
    
    if [ -z "$count" ]; then
        echo -e "${RED}Ошибка: Укажите количество инстансов${NC}"
        exit 1
    fi
    
    echo -e "${YELLOW}Остановка Service2...${NC}"
    stop_service_instances "service2"
    
    echo ""
    echo -e "${GREEN}Запуск Service2 (${count} инстансов)...${NC}"
    scale_service2 $count
}

# === Главная логика ===
COMMAND="${1:-help}"

case "$COMMAND" in
    status)
        show_instances_status
        ;;
    service1)
        scale_service1 $2
        ;;
    service2)
        scale_service2 $2
        ;;
    all)
        scale_all $2 $3
        ;;
    stop-service1)
        stop_service_instances "service1"
        ;;
    stop-service2)
        stop_service_instances "service2"
        ;;
    restart-service1)
        restart_service1 $2
        ;;
    restart-service2)
        restart_service2 $2
        ;;
    help|--help|-h|*)
        show_help
        ;;
esac
