# SOA Lab 3 - Микросервисная архитектура

## Быстрый старт

### 1. Установка всех зависимостей (один раз)
```bash
./setup.sh
```

### 2. Запуск всего проекта
```bash
# Запуск backend (все микросервисы)
./start.sh

# Запуск с фронтендом (dev режим)
./start.sh --dev

# Запуск с фронтендом (production build)
./start.sh --build
```

### 3. Остановка
```bash
./start.sh --stop
```

---

## Что установит setup.sh

- ✅ Проверит Java 11+
- ✅ Проверит Maven
- ✅ Установит и настроит Consul
- ✅ Настроит PostgreSQL и создаст базу `soa_lab3`
- ✅ Соберет все Maven проекты
- ✅ Установит npm зависимости для фронтенда

---

## Что запустит start.sh

### Backend (всегда):
1. **Consul** (порт 8500 HTTP) - Service Discovery для Service1
2. **PostgreSQL** - база данных
3. **Config Server** (порт 8888 HTTP, internal) - централизованная конфигурация
4. **Eureka Server** (порт 8761 HTTP, internal) - Service Discovery для Service2
5. **Zuul Gateway** (порт 8080 HTTPS, public) - API Gateway с SSL termination
6. **Service1** (порт 8082 HTTP, internal) - Jakarta EE + EJB + Embedded Tomcat + Consul
7. **Service2** (порт 8081 HTTP, internal) - Spring Cloud + Eureka + Ribbon

**Архитектура SSL**: Gateway обрабатывает HTTPS снаружи, внутренние сервисы общаются по HTTP

### Frontend (опционально):
- `--dev` - запуск в dev режиме (npm start)
- `--build` - сборка и запуск production версии

---

## Проверка работоспособности

```bash
# Consul UI
open http://localhost:8500

# Eureka Dashboard  
open http://localhost:8761

# API через Gateway
curl http://localhost:8080/api/human-beings
curl http://localhost:8080/api/heroes

# Frontend
open http://localhost:3000
```

---

## Архитектура

```
Frontend (:3000)
    ↓
Zuul Gateway (:8080)
    ├→ Service2 (:8081) [Spring Cloud + Eureka + Ribbon]
    │   └→ Service1 (:8082) [Jakarta EE + EJB + Consul]
    │       └→ PostgreSQL
    │
    └→ Service1 (:8082)

Инфраструктура:
- Config Server (:8888)
- Eureka Server (:8761) 
- Consul (:8500)
```

---

## Конфигурация

Все настройки в файле `config.env`:
- Порты сервисов
- Настройки БД
- SSL (если нужен)

После изменения `config.env`:
```bash
cd scripts && ./generate-configs.sh && cd ..
```

---

## Требования

- Java 11+
- Maven 3.6+
- PostgreSQL 14
- Node.js 16+ (для фронтенда)
