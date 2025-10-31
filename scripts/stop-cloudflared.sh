#!/bin/bash
# Остановка Cloudflare Tunnels

TUNNEL_PID_FILE_GATEWAY="$(dirname "$0")/../logs/cloudflared-gateway.pid"
TUNNEL_PID_FILE_FRONTEND="$(dirname "$0")/../logs/cloudflared-frontend.pid"

echo "Остановка Cloudflare Tunnels..."

# Останавливаем Gateway tunnel
if [ -f "$TUNNEL_PID_FILE_GATEWAY" ]; then
    PID=$(cat "$TUNNEL_PID_FILE_GATEWAY")
    if ps -p "$PID" > /dev/null 2>&1; then
        kill $PID
        echo "✓ Gateway Tunnel остановлен (PID: $PID)"
    fi
    rm "$TUNNEL_PID_FILE_GATEWAY"
fi

# Останавливаем Frontend tunnel
if [ -f "$TUNNEL_PID_FILE_FRONTEND" ]; then
    PID=$(cat "$TUNNEL_PID_FILE_FRONTEND")
    if ps -p "$PID" > /dev/null 2>&1; then
        kill $PID
        echo "✓ Frontend Tunnel остановлен (PID: $PID)"
    fi
    rm "$TUNNEL_PID_FILE_FRONTEND"
fi

# На всякий случай убиваем все процессы cloudflared
pkill -f cloudflared 2>/dev/null || true

echo "✓ Все туннели остановлены"
