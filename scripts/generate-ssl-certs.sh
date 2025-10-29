#!/bin/bash
# Генерация самоподписных SSL сертификатов для всех сервисов

set -e

# Загружаем конфигурацию
cd "$(dirname "$0")"
source ../config.env

SSL_DIR="../ssl"
mkdir -p "$SSL_DIR"

echo "=== Generating Self-Signed SSL Certificates ==="
echo ""

# Проверяем, не существуют ли уже сертификаты
if [ -f "$SSL_DIR/keystore.p12" ]; then
    echo "✓ Certificates already exist"
    exit 0
fi

cd "$SSL_DIR"

echo "Generating keystore for services..."

# Генерируем ключ и сертификат для всех сервисов
keytool -genkeypair \
    -alias "$SSL_KEY_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -storetype PKCS12 \
    -keystore keystore.p12 \
    -storepass "$SSL_KEY_STORE_PASSWORD" \
    -validity 3650 \
    -dname "CN=localhost, OU=SOA, O=ITMO, L=SPB, ST=SPB, C=RU" \
    -ext "SAN=DNS:localhost,IP:127.0.0.1"

echo "✓ Keystore created: ssl/keystore.p12"

# Экспортируем публичный сертификат
keytool -exportcert \
    -alias "$SSL_KEY_ALIAS" \
    -keystore keystore.p12 \
    -storepass "$SSL_KEY_STORE_PASSWORD" \
    -file certificate.crt

echo "✓ Certificate exported: ssl/certificate.crt"

# Создаем truststore
keytool -importcert \
    -alias "$SSL_KEY_ALIAS" \
    -file certificate.crt \
    -keystore truststore.p12 \
    -storepass "$SSL_KEY_STORE_PASSWORD" \
    -storetype PKCS12 \
    -noprompt

echo "✓ Truststore created: ssl/truststore.p12"

echo ""
echo "=== SSL Certificates Generated Successfully ==="
echo ""
echo "Files created:"
echo "  • keystore.p12    - Private key and certificate"
echo "  • truststore.p12  - Trusted certificates"
echo "  • certificate.crt - Public certificate"
echo ""
echo "⚠️  To trust the certificate in your browser:"
echo "   1. Import ssl/certificate.crt into your system/browser"
echo "   2. Mark it as trusted for identifying websites"
echo ""
