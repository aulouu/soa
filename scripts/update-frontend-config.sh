#!/bin/bash
# Обновление конфигурации фронтенда из config.env

set -e

# Загружаем конфигурацию
source ../config.env

FRONTEND_DIR="../frontend"

echo "=== Updating Frontend Configuration ==="
echo ""

PROTOCOL="http"
if [ "$SSL_ENABLED" = "true" ]; then
    PROTOCOL="https"
fi

# Создаем .env файл для React
cat > "$FRONTEND_DIR/.env" << EOF
# Generated from config.env
# Все запросы идут через Zuul Gateway

# API Gateway URL
REACT_APP_API_URL=${PROTOCOL}://localhost:${ZUUL_GATEWAY_PORT}

# Использовать Gateway (true) или прямое подключение к сервисам (false)
REACT_APP_USE_GATEWAY=true

# Прямые URL сервисов (для dev/debug)
REACT_APP_SERVICE1_URL=${PROTOCOL}://localhost:${SERVICE1_PORT}
REACT_APP_SERVICE2_URL=${PROTOCOL}://localhost:${SERVICE2_PORT}
EOF

echo "✓ Created: frontend/.env"

# Создаем .env.local для локальной разработки
cat > "$FRONTEND_DIR/.env.local" << EOF
# Local development configuration
# Эти настройки переопределяют .env в локальной разработке

REACT_APP_API_URL=${PROTOCOL}://localhost:${ZUUL_GATEWAY_PORT}
REACT_APP_USE_GATEWAY=true

# Если нужно тестировать без Gateway, раскомментируйте:
# REACT_APP_USE_GATEWAY=false
# REACT_APP_SERVICE1_URL=${PROTOCOL}://localhost:${SERVICE1_PORT}
# REACT_APP_SERVICE2_URL=${PROTOCOL}://localhost:${SERVICE2_PORT}
EOF

echo "✓ Created: frontend/.env.local"

echo ""
echo "Frontend configuration updated!"
echo ""
echo "API endpoints (through Gateway):"
echo "  • Human Beings: ${PROTOCOL}://localhost:${ZUUL_GATEWAY_PORT}/api/human-beings"
echo "  • Heroes:       ${PROTOCOL}://localhost:${ZUUL_GATEWAY_PORT}/api/heroes"
echo ""
echo "To start frontend:"
echo "  cd frontend && npm install && npm start"
echo ""
