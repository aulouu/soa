#!/bin/bash
# Запуск всего проекта с публичным доступом через Cloudflare
# Одна команда для запуска Backend + Frontend + Cloudflare Tunnels

set -e

echo "╔═══════════════════════════════════════════════════════╗"
echo "║   Запуск SOA Lab3 с публичным доступом                ║"
echo "╚═══════════════════════════════════════════════════════╝"
echo ""

# 1. Включаем Cloudflare в конфиге
if ! grep -q "CLOUDFLARED_ENABLED=true" config.env; then
    echo "Включение Cloudflare Tunnel в config.env..."
    sed -i.bak 's/CLOUDFLARED_ENABLED=false/CLOUDFLARED_ENABLED=true/' config.env
fi

# 2. Запускаем backend (с Cloudflare, т.к. CLOUDFLARED_ENABLED=true)
echo "=== Запуск Backend сервисов (включая Cloudflare Tunnels) ==="
./start.sh --backend

# Backend уже запустил Cloudflare, не нужно запускать заново

# 3. Запускаем Frontend
echo ""
echo "=== Запуск Frontend ==="

source config.env

cd frontend

# Получаем Gateway URL
if [ -f "../cloudflared/tunnel-url-gateway.txt" ]; then
    GATEWAY_URL=$(cat ../cloudflared/tunnel-url-gateway.txt)
    API_URL="$GATEWAY_URL"
else
    API_URL="https://localhost:$ZUUL_GATEWAY_PORT"
fi

# Создаём .env для frontend
cat > .env.local <<EOF
# Автоматически сгенерировано start-public.sh
REACT_APP_API_URL=$API_URL
EOF

echo "Запуск Frontend (dev режим) с API URL: $API_URL"

# Проверяем не запущен ли уже frontend
if [ -f "../logs/frontend.pid" ] && ps -p $(cat ../logs/frontend.pid) > /dev/null 2>&1; then
    echo "✓ Frontend уже запущен"
else
    npm start > ../logs/frontend.log 2>&1 &
    echo $! > ../logs/frontend.pid
    echo "⏳ Ожидание компиляции React приложения (30 секунд)..."
    sleep 30
fi

cd ..

echo ""
echo "╔═══════════════════════════════════════════════════════╗"
echo "║              ✓ ВСЁ ЗАПУЩЕНО!                          ║"
echo "╚═══════════════════════════════════════════════════════╝"
echo ""

# Показываем все URL
if [ -f "cloudflared/tunnel-url-gateway.txt" ]; then
    GATEWAY_URL=$(cat cloudflared/tunnel-url-gateway.txt)
fi

if [ -f "cloudflared/tunnel-url-frontend.txt" ]; then
    FRONTEND_URL=$(cat cloudflared/tunnel-url-frontend.txt)
fi

echo "🌐 ПУБЛИЧНЫЕ URL (доступны из интернета):"
echo ""
echo "  ✨ Frontend:      $FRONTEND_URL"
echo "  🔗 API Gateway:   $GATEWAY_URL"
echo ""
echo "📝 Примеры API запросов:"
echo "  curl $GATEWAY_URL/api/human-beings"
echo "  curl $GATEWAY_URL/api/heroes"
echo ""
echo "🖥️  Локальные URL:"
echo "  Frontend:      http://localhost:3000"
echo "  Zuul Gateway:  https://localhost:8080"
echo "  Eureka:        http://localhost:8761"
echo "  Consul:        http://localhost:8500"
echo ""
echo "⚠️  ВАЖНО: Если Frontend не работает:"
echo "  1. Подождите ещё 10-15 секунд (React компилируется)"
echo "  2. Откройте $FRONTEND_URL в браузере"
echo "  3. Сделайте Hard Refresh: Ctrl+Shift+R (Windows/Linux) или Cmd+Shift+R (Mac)"
echo ""
echo "📋 Для остановки всех сервисов:"
echo "  ./start.sh --stop"
echo ""
