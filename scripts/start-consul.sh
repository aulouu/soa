#!/bin/bash
# Скрипт запуска Consul в dev режиме

set -e

CONSUL_DIR="../consul"
LOG_DIR="../logs"

# Создаем директорию для логов
mkdir -p "$LOG_DIR"

# Проверяем, установлен ли Consul
if [ ! -f "$CONSUL_DIR/consul" ]; then
    echo "Consul not found! Running install script..."
    ./install-consul.sh
fi

# Проверяем, не запущен ли уже Consul
#if pgrep -x "consul" > /dev/null; then
if ps -W | grep -q "consul"; then
    echo "Consul is already running"
    exit 0
fi

echo "=== Starting Consul in dev mode ==="
echo "UI will be available at: http://localhost:8500"

cd "$CONSUL_DIR"

# Запускаем Consul в dev режиме
nohup ./consul agent -dev \
    -ui \
    -client=0.0.0.0 \
    -bind=127.0.0.1 \
    > ../logs/consul.log 2>&1 &

CONSUL_PID=$!
echo $CONSUL_PID > ../logs/consul.pid

echo "Consul started with PID: $CONSUL_PID"
echo "Log file: logs/consul.log"

# Ждем, пока Consul запустится
sleep 3

# Проверяем статус
#if pgrep -x "consul" > /dev/null; then
if ps -W | grep -q "consul"; then
    echo "✓ Consul is running"
    ./consul members
else
    echo "✗ Failed to start Consul"
    exit 1
fi
