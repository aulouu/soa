#!/bin/bash
# Скрипт установки Mule Runtime Community Edition

set -e

MULE_VERSION="4.4.0"
MULE_DOWNLOAD_URL="https://repository-master.mulesoft.org/nexus/content/repositories/releases/org/mule/distributions/mule-standalone/${MULE_VERSION}/mule-standalone-${MULE_VERSION}.tar.gz"
MULE_DIR="$(pwd)"

echo "📦 Downloading Mule Runtime CE ${MULE_VERSION}..."
echo "URL: ${MULE_DOWNLOAD_URL}"

if [ -f "mule-standalone-${MULE_VERSION}.tar.gz" ]; then
    echo "✓ Archive already downloaded"
else
    curl -L -o "mule-standalone-${MULE_VERSION}.tar.gz" "${MULE_DOWNLOAD_URL}"
    echo "✓ Download complete"
fi

if [ -d "mule-standalone-${MULE_VERSION}" ]; then
    echo "✓ Mule already extracted"
else
    echo "📂 Extracting Mule Runtime..."
    tar -xzf "mule-standalone-${MULE_VERSION}.tar.gz"
    echo "✓ Extraction complete"
fi

# Создаем симлинк для удобства
if [ -L "mule" ]; then
    rm mule
fi
ln -s "mule-standalone-${MULE_VERSION}" mule

echo "✅ Mule Runtime installed successfully!"
echo "📁 Location: ${MULE_DIR}/mule"
echo ""
echo "To start Mule:"
echo "  ${MULE_DIR}/mule/bin/mule start"
echo ""
echo "To deploy apps:"
echo "  cp your-app.jar ${MULE_DIR}/mule/apps/"
