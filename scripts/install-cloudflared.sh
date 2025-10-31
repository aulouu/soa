#!/bin/bash
# Установка cloudflared для macOS

set -e

echo "=== Установка Cloudflared ==="

# Проверяем, установлен ли уже cloudflared
if command -v cloudflared &> /dev/null; then
    echo "✓ Cloudflared уже установлен"
    cloudflared --version
    exit 0
fi

# Определяем архитектуру
ARCH=$(uname -m)
if [ "$ARCH" = "arm64" ]; then
    ARCH="arm64"
elif [ "$ARCH" = "x86_64" ]; then
    ARCH="amd64"
else
    echo "❌ Неподдерживаемая архитектура: $ARCH"
    exit 1
fi

# Создаём директорию для cloudflared если её нет
mkdir -p ../cloudflared

# Скачиваем cloudflared
echo "Скачивание cloudflared для macOS ($ARCH)..."
DOWNLOAD_URL="https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-darwin-$ARCH"

curl -L "$DOWNLOAD_URL" -o ../cloudflared/cloudflared

# Делаем исполняемым
chmod +x ../cloudflared/cloudflared

# Создаём симлинк для удобства
sudo ln -sf "$(pwd)/../cloudflared/cloudflared" /usr/local/bin/cloudflared 2>/dev/null || true

echo "✓ Cloudflared установлен"
cloudflared --version
