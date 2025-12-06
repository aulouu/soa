# SOA Lab 4 - SOAP + Mule ESB Integration

## 🎯 Что изменилось в Lab4

### Новая архитектура:
```
Frontend (:3000)
    ↓
Zuul Gateway (:8080)
    ↓
Service2 - Heroes (:8091) [REST - Spring Cloud]
    ↓
🆕 Mule ESB (:8083) [Интеграционная шина]
    ↓
🆕 REST-adapter (:9090) [REST → SOAP трансляция]
    ↓
🆕 Service1 SOAP (:8082) [Jakarta EE + JAX-WS]
    ↓
PostgreSQL (:5432)
```

### Ключевые изменения:

1. **Service1 переписан с REST на SOAP**
   - Был: REST API (JAX-RS) на WildFly
   - Стало: SOAP Web Service (JAX-WS) на WildFly
   - EJB бизнес-логика осталась без изменений
   - WSDL доступен: `http://localhost:8082/service1-soap/HumanBeingService?wsdl`

2. **Добавлен Mule ESB** (порт 8083)
   - Полноценный Mule Runtime Community Edition 4.4.0
   - Играет роль интеграционной шины между Service2 и Service1
   - Принимает REST запросы от Service2
   - Проксирует к REST-adapter

3. **Добавлен REST-adapter** (порт 9090)
   - Spring Boot приложение
   - Обеспечивает обратную совместимость REST API
   - Транслирует REST → SOAP запросы к Service1
   - Позволяет фронтенду и Service2 работать без изменений

4. **Service2 обновлен**
   - Теперь вызывает Mule ESB вместо прямого вызова Service1
   - URL изменен с `localhost:8082` на `localhost:8091`
   - Никаких других изменений в коде

## 🚀 Быстрый старт

### 1. Сборка всех компонентов

```bash
# Service1 (SOAP)
cd service1
mvn clean install -DskipTests

# Service1 REST-adapter
cd ../service1-rest-adapter
mvn clean package -DskipTests

# Service2 (обновленный)
cd ../service2
mvn clean package -DskipTests

# Mule приложение уже собрано и задеплоено
```

### 2. Запуск системы

```bash
# Запуск всех сервисов Lab4
./start-lab4.sh

# С фронтендом (dev режим)
./start-lab4.sh --dev

# С фронтендом (production build)
./start-lab4.sh --build
```

### 3. Остановка

```bash
./start-lab4.sh --stop
```

## 🔍 Проверка работоспособности

### 1. Проверка SOAP Service1

```bash
# Получить WSDL
curl http://localhost:8082/service1-soap/HumanBeingService?wsdl

# SOAP запрос через curl
curl -X POST http://localhost:8082/service1-soap/HumanBeingService \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0"?>
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
               xmlns:ns="http://soap.service1.soa.itmo/">
  <soap:Body>
    <ns:getAllHumanBeings>
      <page>0</page>
      <size>10</size>
    </ns:getAllHumanBeings>
  </soap:Body>
</soap:Envelope>'
```

### 2. Проверка REST-adapter

```bash
# REST-adapter транслирует в SOAP
curl http://localhost:9090/api/human-beings?page=0&size=10
```

### 3. Проверка Mule ESB

```bash
# Mule ESB проксирует к REST-adapter
curl http://localhost:8083/api/human-beings?page=0&size=10
```

### 4. Проверка Service2 → Mule ESB

```bash
# Service2 вызывает Mule ESB
curl http://localhost:8091/api/heroes
```

### 5. Проверка через Gateway

```bash
# Полная цепочка: Gateway → Service2 → Mule → REST-adapter → SOAP
curl http://localhost:8080/api/heroes
```

## 📁 Структура проекта (обновленная)

```
soa/
├── service1/                    # Service1 (SOAP)
│   ├── service1-ejb/           # EJB бизнес-логика (без изменений)
│   ├── service1-soap/          # 🆕 SOAP Web Service (JAX-WS)
│   ├── service1-web/           # Старый REST API (не используется)
│   └── service1-eureka-client/ # Eureka прокси
├── service1-rest-adapter/       # 🆕 REST → SOAP адаптер
│   ├── pom.xml
│   └── src/main/java/itmo/soa/adapter/
│       ├── RestAdapterApplication.java
│       ├── controller/HumanBeingController.java  # REST endpoints
│       ├── client/SoapClientService.java         # SOAP клиент
│       └── model/                                 # DTO модели
├── mule-runtime/                # 🆕 Mule Runtime CE 4.4.0
│   ├── mule/                    # Установленный Mule
│   │   ├── apps/                # Задеплоенные приложения
│   │   │   └── mule-integration-app-1.0.0.jar
│   │   └── bin/mule             # Запуск Mule
│   └── install-mule.sh
├── mule-integration-app/        # 🆕 Mule приложение
│   ├── pom.xml
│   └── src/main/mule/
│       └── integration-flow.xml # Mule flows (HTTP proxy)
├── service2/                    # Service2 (обновлен для Mule ESB)
├── zuul-gateway/                # API Gateway (без изменений)
├── eureka-server/               # Service Discovery (без изменений)
├── config-server/               # Config Server (без изменений)
├── consul/                      # Consul (без изменений)
├── frontend/                    # React Frontend (без изменений)
├── start-lab4.sh                # 🆕 Скрипт запуска Lab4
├── config.env                   # Обновленная конфигурация
└── README-LAB4.md               # Эта документация
```

## 🔧 Технические детали

### SOAP Service1 (service1-soap)

**Технологии:**
- Jakarta EE 10
- JAX-WS 3.0 (для SOAP)
- EJB 3.2 (бизнес-логика)
- WildFly 33 (Application Server)

**Endpoints:**
- `getAllHumanBeings()`
- `getHumanBeingById(Long id)`
- `createHumanBeing(HumanBeing)`
- `updateHumanBeing(Long id, HumanBeing)`
- `deleteHumanBeing(Long id)`
- `countByMood(int moodValue)`
- `getUniqueImpactSpeeds()`
- `countByNameStartsWith(String prefix)`

**WSDL Location:**
```
http://localhost:8082/service1-soap/HumanBeingService?wsdl
```

### REST-adapter

**Технологии:**
- Spring Boot 2.3
- JAX-WS Client (для SOAP вызовов)
- JAXB (для XML/SOAP маршалинга)

**Функции:**
- Принимает REST запросы (совместим с API Service1)
- Создает SOAP сообщения
- Вызывает SOAP Service1
- Парсит SOAP ответы
- Возвращает JSON

### Mule ESB

**Версия:** Mule Runtime CE 4.4.0

**Компоненты:**
- HTTP Listener (порт 8083)
- HTTP Request (проксирует к REST-adapter)
- Logging

**Flow:**
```xml
HTTP Listener (:8081)
    ↓
Logger (входящий запрос)
    ↓
HTTP Request → REST-adapter (:9090)
    ↓
Logger (ответ)
    ↓
Return response
```

## 🎓 Соответствие заданию Lab4

✅ **Первый сервис переписан по протоколу SOAP**
- Service1 теперь использует JAX-WS
- WSDL доступен и валиден

✅ **Сервис развернут на сервере приложений**
- WildFly 33 (Jakarta EE certified)
- Порт 8082

✅ **Второй сервис не модифицирован**
- Service2 работает без изменений в API
- Изменен только URL для вызова (с 8082 на 8091)

✅ **Установлен и настроен Mule ESB**
- Mule Runtime CE 4.4.0
- Работает локально
- Приложение задеплоено

✅ **Интеграция через Mule ESB**
- Service2 → Mule ESB → REST-adapter → SOAP Service1
- Прозрачная интеграция

✅ **REST-прослойка для клиента**
- REST-adapter обеспечивает REST API
- Фронтенд работает без изменений
- Только трансляция протоколов, без бизнес-логики

## 📊 Порты (Lab4)

| Компонент | Порт     | Описание |
|-----------|----------|----------|
| Frontend | 3000     | React приложение |
| Zuul Gateway | 8080     | API Gateway (HTTPS) |
| **Mule ESB** | **8083** | 🆕 Интеграционная шина |
| Service1 SOAP | 8082     | 🆕 SOAP Web Service (WildFly) |
| Service1 Eureka Proxy | 8089     | Прокси для Service Discovery |
| **REST-adapter** | **9090** | 🆕 REST → SOAP адаптер |
| Service2 (Heroes) | 8091     | Spring Cloud сервис |
| Config Server | 8888     | Централизованная конфигурация |
| Eureka Server | 8761     | Service Registry |
| Consul | 8500     | Service Discovery + Health |
| PostgreSQL | 5432     | База данных |

## 🧪 Тестирование полного flow

```bash
# 1. Проверка WSDL
curl http://localhost:8082/service1-soap/HumanBeingService?wsdl

# 2. Прямой SOAP вызов
curl -X POST http://localhost:8082/service1-soap/HumanBeingService \
  -H "Content-Type: text/xml" \
  -d '<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns="http://soap.service1.soa.itmo/">
<soap:Body><ns:getAllHumanBeings><page>0</page><size>5</size></ns:getAllHumanBeings></soap:Body>
</soap:Envelope>'

# 3. Через REST-adapter
curl "http://localhost:9090/api/human-beings?page=0&size=5"

# 4. Через Mule ESB
curl "http://localhost:8083/api/human-beings?page=0&size=5"

# 5. Через Service2
curl http://localhost:8091/api/heroes

# 6. Через Gateway (полная цепочка)
curl http://localhost:8080/api/heroes
```

## 🐛 Troubleshooting

### Mule ESB не запускается

```bash
# Проверить логи
tail -f logs/mule.log

# Проверить что Java 17 используется
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Перезапустить Mule вручную
cd mule-runtime/mule
bin/mule start
```

### SOAP Service1 не отвечает

```bash
# Проверить WildFly логи
tail -f logs/service1-wildfly.log

# Проверить деплоймент
ls -la wildfly-33.0.1.Final/standalone/deployments/

# Проверить WSDL
curl http://localhost:8082/service1-soap/HumanBeingService?wsdl
```

### REST-adapter ошибки

```bash
# Проверить логи
tail -f logs/rest-adapter.log

# Проверить что SOAP Service1 запущен
curl http://localhost:8082/service1-soap/HumanBeingService?wsdl
```

## 📝 Заметки

- **Mule ESB** работает на Java 17
- **WildFly** работает на Java 17
- **Service2** работает на Java 11
- Все сервисы запускаются локально
- Фронтенд работает без изменений
- EJB бизнес-логика не изменилась

## 🚀 Что дальше?

Система готова для демонстрации Lab4:
- ✅ SOAP сервис работает
- ✅ Mule ESB интегрирует сервисы
- ✅ REST-прослойка обеспечивает совместимость
- ✅ Фронтенд работает без изменений
- ✅ Полная цепочка вызовов функционирует

Для тестирования используйте `start-lab4.sh` и проверяйте каждый уровень архитектуры отдельно.
