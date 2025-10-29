#!/bin/bash
# Скрипт установки всех зависимостей (запускать один раз)

set -e

echo "╔═══════════════════════════════════════════════════════╗"
echo "║      Установка зависимостей SOA Lab 3                ║"
echo "╚═══════════════════════════════════════════════════════╝"
echo ""

source config.env

# 1. Java
echo "=== 1/6: Java ===" 
if command -v java &> /dev/null; then
    JAVA_VER=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VER" -ge 11 ]; then
        echo "✓ Java $JAVA_VER"
    else
        echo "❌ Нужна Java 11+. Установите: brew install openjdk@11"
        exit 1
    fi
else
    echo "❌ Java не найдена: brew install openjdk@11"
    exit 1
fi

# 2. Maven
echo "=== 2/6: Maven ==="
if command -v mvn &> /dev/null; then
    echo "✓ Maven"
else
    echo "❌ Maven не найден: brew install maven"
    exit 1
fi

# 3. Consul
echo "=== 3/6: Consul ==="
cd scripts
[ ! -f ../consul/consul ] && ./install-consul.sh || echo "✓ Consul"
cd ..

# 5. Сборка проектов
echo "=== 5/6: Сборка проектов ==="
cd scripts && ./generate-configs.sh && cd ..

echo "Сборка Config Server..."
cd config-server && mvn clean install -DskipTests -q && cd ..

echo "Сборка Eureka Server..."
cd eureka-server && mvn clean install -DskipTests -q && cd ..

echo "Сборка Zuul Gateway..."
cd zuul-gateway && mvn clean install -DskipTests -q && cd ..

echo "Сборка Service1 (EJB + Web + Runner)..."
(cd service1 && mvn clean package -DskipTests -q) || echo "⚠️ Service1 build failed"

echo "Сборка Service2..."
(cd service2 && mvn clean install -DskipTests -q) || echo "⚠️ Service2 build failed"

echo "✓ Все проекты собраны"

# 6. Frontend
echo "=== 6/6: Frontend ==="
if command -v npm &> /dev/null && [ -d "frontend" ]; then
    cd frontend
    [ ! -d "node_modules" ] && npm install || echo "✓ npm зависимости"
    cd ..
else
    echo "⚠️  Node.js не найден (фронтенд не установлен)"
fi

echo ""
echo "╔═══════════════════════════════════════════════════════╗"
echo "║         ✓ Установка завершена!                       ║"
echo "╚═══════════════════════════════════════════════════════╝"
echo ""
echo "Запустите проект: ./start.sh"
echo ""
