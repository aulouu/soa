#!/bin/bash
# Остановка Consul

set -e

PID_FILE="../logs/consul.pid"

if [ -f "$PID_FILE" ]; then
    PID=$(cat "$PID_FILE")
    echo "Stopping Consul (PID: $PID)..."
    kill $PID 2>/dev/null || true
    rm "$PID_FILE"
    echo "Consul stopped"
else
    echo "Consul PID file not found, trying to kill by name..."
    pkill consul || echo "Consul not running"
fi
