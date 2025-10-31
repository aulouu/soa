#!/bin/bash
# Запуск Cloudflare Tunnel для Gateway и Frontend

set -e

cd "$(dirname "$0")/.."
source config.env

TUNNEL_LOG_FILE_GATEWAY="logs/cloudflared-gateway.log"
TUNNEL_PID_FILE_GATEWAY="logs/cloudflared-gateway.pid"

TUNNEL_LOG_FILE_FRONTEND="logs/cloudflared-frontend.log"
TUNNEL_PID_FILE_FRONTEND="logs/cloudflared-frontend.pid"

# Проверяем, установлен ли cloudflared
if ! command -v cloudflared &> /dev/null; then
    echo "❌ Cloudflared не установлен. Запустите: ./setup.sh"
    exit 1
fi

mkdir -p cloudflared

echo "╔═══════════════════════════════════════════════════════╗"
echo "║      Запуск Cloudflare Tunnels                       ║"
echo "╚═══════════════════════════════════════════════════════╝"
echo ""

# ========== TUNNEL 1: Zuul Gateway ==========
echo "=== 1/2: Cloudflare Tunnel для Zuul Gateway ==="

# Проверяем, не запущен ли уже
if [ -f "$TUNNEL_PID_FILE_GATEWAY" ]; then
    PID=$(cat "$TUNNEL_PID_FILE_GATEWAY")
    if ps -p "$PID" > /dev/null 2>&1; then
        echo "✓ Gateway tunnel уже запущен (PID: $PID)"
        GATEWAY_URL=$(cat cloudflared/tunnel-url-gateway.txt 2>/dev/null || echo "")
    fi
fi

if [ -z "$GATEWAY_URL" ]; then
    # Запускаем туннель для Gateway
    nohup cloudflared tunnel --url https://localhost:$ZUUL_GATEWAY_PORT --no-tls-verify > "$TUNNEL_LOG_FILE_GATEWAY" 2>&1 &
    GATEWAY_PID=$!
    echo $GATEWAY_PID > "$TUNNEL_PID_FILE_GATEWAY"
    
    echo "Ожидание инициализации Gateway tunnel..."
    sleep 7
    
    # Извлекаем URL
    GATEWAY_URL=$(grep -oE "https://[a-z0-9-]+\.trycloudflare\.com" "$TUNNEL_LOG_FILE_GATEWAY" | tail -1)
    
    if [ -n "$GATEWAY_URL" ]; then
        echo "$GATEWAY_URL" > cloudflared/tunnel-url-gateway.txt
        echo "✓ Gateway Tunnel: $GATEWAY_URL"
        echo "  PID: $GATEWAY_PID"
    else
        echo "⚠️  Не удалось получить URL Gateway. Логи: $TUNNEL_LOG_FILE_GATEWAY"
    fi
fi

echo ""

# ========== TUNNEL 2: Frontend ==========
echo "=== 2/2: Cloudflare Tunnel для Frontend ==="

# Проверяем, не запущен ли уже
if [ -f "$TUNNEL_PID_FILE_FRONTEND" ]; then
    PID=$(cat "$TUNNEL_PID_FILE_FRONTEND")
    if ps -p "$PID" > /dev/null 2>&1; then
        echo "✓ Frontend tunnel уже запущен (PID: $PID)"
        FRONTEND_URL=$(cat cloudflared/tunnel-url-frontend.txt 2>/dev/null || echo "")
    fi
fi

if [ -z "$FRONTEND_URL" ]; then
    # Запускаем туннель для Frontend
    nohup cloudflared tunnel --url http://localhost:$FRONTEND_PORT > "$TUNNEL_LOG_FILE_FRONTEND" 2>&1 &
    FRONTEND_PID=$!
    echo $FRONTEND_PID > "$TUNNEL_PID_FILE_FRONTEND"
    
    echo "Ожидание инициализации Frontend tunnel..."
    sleep 7
    
    # Извлекаем URL
    FRONTEND_URL=$(grep -oE "https://[a-z0-9-]+\.trycloudflare\.com" "$TUNNEL_LOG_FILE_FRONTEND" | tail -1)
    
    if [ -n "$FRONTEND_URL" ]; then
        echo "$FRONTEND_URL" > cloudflared/tunnel-url-frontend.txt
        echo "✓ Frontend Tunnel: $FRONTEND_URL"
        echo "  PID: $FRONTEND_PID"
    else
        echo "⚠️  Не удалось получить URL Frontend. Логи: $TUNNEL_LOG_FILE_FRONTEND"
    fi
fi

echo ""

# ========== Обновляем конфиг Frontend ==========
if [ -n "$GATEWAY_URL" ] && [ -d "frontend" ]; then
    cat > frontend/.env.local <<EOF
# Автоматически сгенерировано start-cloudflared.sh
REACT_APP_API_URL=$GATEWAY_URL
EOF
    echo "✓ Frontend настроен на использование Gateway URL: $GATEWAY_URL"
fi

echo ""
echo "╔═══════════════════════════════════════════════════════╗"
echo "║         ✓ Cloudflare Tunnels запущены!               ║"
echo "╚═══════════════════════════════════════════════════════╝"
echo ""
echo "🌐 Публичные URL:"
echo "  Gateway (API): $GATEWAY_URL"
echo "  Frontend:      $FRONTEND_URL"
echo ""
echo "📝 Примеры запросов:"
echo "  curl $GATEWAY_URL/api/human-beings"
echo "  curl $GATEWAY_URL/api/heroes"
echo ""
echo "Для остановки: ./start.sh --stop"
echo ""
