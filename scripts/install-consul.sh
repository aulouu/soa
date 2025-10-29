#!/bin/bash
# Универсальный скрипт установки Consul

set -e

CONSUL_VERSION="1.22.0"

echo "=== Installing Consul ==="

# Создаем директорию для Consul
mkdir -p ../consul
cd ../consul

# Проверяем, установлен ли уже Consul
if [ -f "consul" ] && ./consul version &>/dev/null; then
    echo "✓ Consul already installed: $(./consul version | head -1)"
    exit 0
fi

# Вариант 1: Попробуем системный пакетный менеджер
if command -v apt-get &> /dev/null; then
    echo "Attempting to install via apt-get..."
    if sudo apt-get update && sudo apt-get install -y consul; then
        ln -sf $(which consul) ./consul
        echo "✓ Consul installed via apt-get"
        exit 0
    fi
fi

if command -v yum &> /dev/null; then
    echo "Attempting to install via yum..."
    if sudo yum install -y consul; then
        ln -sf $(which consul) ./consul
        echo "✓ Consul installed via yum"
        exit 0
    fi
fi

if command -v brew &> /dev/null; then
    echo "Attempting to install via brew..."
    if brew install consul; then
        ln -sf $(which consul) ./consul
        echo "✓ Consul installed via brew"
        exit 0
    fi
fi

# Вариант 2: Скачиваем бинарник напрямую
echo "Installing from binary..."

OS=$(uname -s | tr '[:upper:]' '[:lower:]')
ARCH=$(uname -m)

# Определяем архитектуру
case "$ARCH" in
    x86_64) ARCH="amd64" ;;
    aarch64) ARCH="arm64" ;;
    arm64) ARCH="arm64" ;;
    i386|i686) ARCH="386" ;;
    *) echo "Unsupported architecture: $ARCH"; exit 1 ;;
esac

echo "Detected: ${OS}_${ARCH}"

# Формируем URL
CONSUL_URL="https://releases.hashicorp.com/consul/${CONSUL_VERSION}/consul_${CONSUL_VERSION}_${OS}_${ARCH}.zip"
echo "Downloading from: $CONSUL_URL"

# Скачиваем
if command -v wget &> /dev/null; then
    wget -O consul.zip "$CONSUL_URL" || { echo "Download failed"; exit 1; }
elif command -v curl &> /dev/null; then
    curl -fL "$CONSUL_URL" -o consul.zip || { echo "Download failed"; exit 1; }
else
    echo "❌ Need wget or curl to download"
    exit 1
fi

# Проверяем размер файла
if [ ! -s consul.zip ] || [ $(wc -c < consul.zip) -lt 10000 ]; then
    echo "❌ Downloaded file is too small or empty (probably 404 error)"
    cat consul.zip 2>/dev/null | head -5
    rm -f consul.zip
    echo ""
    echo "Please install Consul manually:"
    echo "  1. Download from https://www.consul.io/downloads"
    echo "  2. Extract to: $(pwd)/consul"
    echo "  3. Make executable: chmod +x consul"
    exit 1
fi

# Распаковываем
if command -v unzip &> /dev/null; then
    unzip -q consul.zip || { echo "Unzip failed"; exit 1; }
else
    echo "❌ unzip command not found. Please install unzip."
    exit 1
fi

rm -f consul.zip
chmod +x consul

echo "✓ Consul installed successfully!"
./consul version
