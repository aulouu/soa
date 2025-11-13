#!/bin/bash
# Скрипт нагрузочного тестирования для проверки балансировки нагрузки

GATEWAY_URL="${1:-https://localhost:8080}"
REQUESTS_PER_ENDPOINT="${2:-50}"

# Цвета
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m'

echo -e "${CYAN}╔═══════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║        Нагрузочное тестирование Load Balancing        ║${NC}"
echo -e "${CYAN}╚═══════════════════════════════════════════════════════╝${NC}"
echo ""
echo -e "${BLUE}Gateway URL:${NC} $GATEWAY_URL"
echo -e "${BLUE}Запросов на эндпоинт:${NC} $REQUESTS_PER_ENDPOINT"
echo ""

# Создаем временный файл для сбора статистики
STATS_FILE=$(mktemp)
trap "rm -f $STATS_FILE" EXIT

# Счетчики
total_requests=0
successful_requests=0
failed_requests=0

# Функция для отправки запроса и сбора статистики
send_request() {
    local method=$1
    local path=$2
    local endpoint_name=$3
    
    start_time=$(date +%s%N)
    
    if [[ "$method" == "POST" ]] || [[ "$method" == "PUT" ]]; then
        # Для POST/PUT запросов нужно тело
        response=$(curl -k -s -w "\n%{http_code}\n%{time_total}" -X $method \
            -H "Content-Type: application/json" \
            -d '{"name":"LoadTest","coordinates":{"x":1,"y":1},"hasToothpick":true,"impactSpeed":5,"weaponType":"RIFLE","mood":"APATHY","car":{"cool":true}}' \
            "$GATEWAY_URL$path" 2>/dev/null)
    else
        response=$(curl -k -s -w "\n%{http_code}\n%{time_total}" -X $method "$GATEWAY_URL$path" 2>/dev/null)
    fi
    
    http_code=$(echo "$response" | tail -n 2 | head -n 1)
    time_total=$(echo "$response" | tail -n 1)
    
    end_time=$(date +%s%N)
    duration=$(( (end_time - start_time) / 1000000 ))
    
    total_requests=$((total_requests + 1))
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        successful_requests=$((successful_requests + 1))
        echo "$endpoint_name|$http_code|$duration" >> $STATS_FILE
        echo -e -n "${GREEN}.${NC}"
    else
        failed_requests=$((failed_requests + 1))
        echo -e -n "${RED}x${NC}"
    fi
}

# Массив эндпоинтов для тестирования
declare -a ENDPOINTS=(
    "GET|/api/human-beings|Get All Human Beings"
    "GET|/api/human-beings?page=0&size=5|Get Human Beings (pagination)"
    "GET|/api/human-beings/statistics/mood-count/1|Statistics: Mood Count"
    "GET|/api/human-beings/statistics/name/starts-with/L|Statistics: Name Prefix"
    "GET|/api/heroes|Get All Heroes"
    "GET|/api/heroes?page=0&size=5|Get Heroes (pagination)"
)

echo -e "${YELLOW}=== Начало нагрузочного тестирования ===${NC}"
echo ""

# Запускаем тесты для каждого эндпоинта
for endpoint_data in "${ENDPOINTS[@]}"; do
    IFS='|' read -r method path name <<< "$endpoint_data"
    
    echo -e "${BLUE}Тестирую:${NC} $method $name"
    echo -n "  Прогресс: "
    
    for i in $(seq 1 $REQUESTS_PER_ENDPOINT); do
        send_request "$method" "$path" "$name" &
        
        # Ограничиваем количество параллельных запросов
        if [ $((i % 10)) -eq 0 ]; then
            wait
        fi
    done
    
    wait
    echo ""
    echo ""
done

echo ""
echo -e "${CYAN}╔═══════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║                 Результаты тестирования               ║${NC}"
echo -e "${CYAN}╚═══════════════════════════════════════════════════════╝${NC}"
echo ""

# Общая статистика
echo -e "${BLUE}=== Общая статистика ===${NC}"
echo -e "  Всего запросов:      ${CYAN}$total_requests${NC}"
echo -e "  Успешных:            ${GREEN}$successful_requests${NC}"
echo -e "  Неудачных:           ${RED}$failed_requests${NC}"
echo -e "  Success Rate:        ${GREEN}$(awk "BEGIN {printf \"%.2f%%\", ($successful_requests/$total_requests)*100}")${NC}"
echo ""

# Статистика по эндпоинтам
echo -e "${BLUE}=== Статистика по эндпоинтам ===${NC}"
echo ""

while IFS='|' read -r endpoint http_code duration; do
    echo "$endpoint"
done < $STATS_FILE | sort | uniq -c | while read count endpoint; do
    avg_time=$(grep "^$endpoint|" $STATS_FILE | cut -d'|' -f3 | awk '{sum+=$1; count++} END {if(count>0) print sum/count; else print 0}')
    printf "  %-50s %s запросов (avg: %s ms)\n" "$endpoint" "${CYAN}$count${NC}" "${YELLOW}$(printf "%.0f" $avg_time)${NC}"
done

echo ""

# Анализ времени ответа
echo -e "${BLUE}=== Анализ времени ответа (ms) ===${NC}"
min_time=$(cut -d'|' -f3 $STATS_FILE | sort -n | head -1)
max_time=$(cut -d'|' -f3 $STATS_FILE | sort -n | tail -1)
avg_time=$(cut -d'|' -f3 $STATS_FILE | awk '{sum+=$1; count++} END {print sum/count}')

echo -e "  Минимальное:  ${GREEN}$min_time ms${NC}"
echo -e "  Максимальное: ${RED}$max_time ms${NC}"
echo -e "  Среднее:      ${YELLOW}$(printf "%.2f" $avg_time) ms${NC}"
echo ""

# Проверка распределения нагрузки через логи
echo -e "${BLUE}=== Проверка балансировки нагрузки ===${NC}"
echo ""
echo -e "${YELLOW}Анализ логов сервисов...${NC}"
echo ""

# Service1 instances
echo -e "${GREEN}Service1 (Human Beings):${NC}"
for i in 1 2; do
    if [ -f "logs/service1-instance${i}.log" ]; then
        # Считаем количество обработанных запросов по логам REST endpoints
        count=$(grep -c "REST endpoint" logs/service1-instance${i}.log 2>/dev/null || echo "0")
        echo -e "  Instance $i (порт 808$((i+1))): ${CYAN}$count${NC} запросов обработано"
    fi
done
echo ""

# Service2 instances
echo -e "${GREEN}Service2 (Heroes):${NC}"
for i in 1 2; do
    if [ -f "logs/service2-instance${i}.log" ]; then
        # Считаем количество HTTP запросов в логах Spring Boot (DispatcherServlet)
        count=$(grep -E "GET.*heroes|Mapped to.*heroes" logs/service2-instance${i}.log 2>/dev/null | wc -l | tr -d ' ')
        # Альтернативный подсчет через Tomcat access log
        if [ "$count" = "0" ]; then
            count=$(grep -c "heroes" logs/service2-instance${i}.log 2>/dev/null || echo "0")
        fi
        echo -e "  Instance $i (порт 809$((i))): ${CYAN}$count${NC} запросов обработано"
    fi
done
echo ""

# Проверка Eureka
echo -e "${BLUE}=== Проверка Service Discovery ===${NC}"
echo ""
echo -e "Проверяю зарегистрированные инстансы в Eureka..."
eureka_status=$(curl -s http://localhost:8761/eureka/apps | grep -o '<app>[^<]*</app>' | wc -l || echo "0")
if [ "$eureka_status" -gt 0 ]; then
    echo -e "${GREEN}✓${NC} Eureka доступна"
    
    # Показываем зарегистрированные сервисы
    service1_count=$(curl -s http://localhost:8761/eureka/apps/HUMAN-BEINGS-SERVICE 2>/dev/null | grep -c '<instance>' || echo "0")
    service2_count=$(curl -s http://localhost:8761/eureka/apps/HEROES-SERVICE 2>/dev/null | grep -c '<instance>' || echo "0")
    
    echo -e "  ${CYAN}HUMAN-BEINGS-SERVICE:${NC} $service1_count инстансов"
    echo -e "  ${CYAN}HEROES-SERVICE:${NC}       $service2_count инстансов"
else
    echo -e "${RED}✗${NC} Eureka недоступна"
fi
echo ""

# Проверка Consul
echo -e "Проверяю зарегистрированные инстансы в Consul..."
consul_status=$(curl -s http://localhost:8500/v1/catalog/services 2>/dev/null)
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓${NC} Consul доступен"
    
    service1_consul=$(curl -s http://localhost:8500/v1/catalog/service/service1-web 2>/dev/null | grep -c '"ServiceID"' || echo "0")
    echo -e "  ${CYAN}service1-web:${NC}         $service1_consul инстансов"
else
    echo -e "${RED}✗${NC} Consul недоступен"
fi
echo ""

echo -e "${CYAN}╔═══════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║              Тестирование завершено!                  ║${NC}"
echo -e "${CYAN}╚═══════════════════════════════════════════════════════╝${NC}"
echo ""

# Рекомендации
if [ $failed_requests -gt 0 ]; then
    echo -e "${YELLOW}⚠️  Обнаружены неудачные запросы. Проверьте логи сервисов:${NC}"
    echo -e "  tail -f logs/service1-instance*.log"
    echo -e "  tail -f logs/service2-instance*.log"
    echo -e "  tail -f logs/zuul-gateway.log"
    echo ""
fi

if [ $successful_requests -eq $total_requests ]; then
    echo -e "${GREEN}🎉 Все запросы успешно обработаны!${NC}"
    echo ""
fi

echo -e "${BLUE}Для мониторинга в реальном времени:${NC}"
echo -e "  ./scale.sh status              # Статус инстансов"
echo -e "  open http://localhost:8761     # Eureka Dashboard"
echo -e "  open http://localhost:8500     # Consul UI"
echo ""
