#!/bin/bash
# SSL environment для JVM

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TRUSTSTORE_PATH="${PROJECT_DIR}/ssl/truststore.p12"

export JAVA_SSL_OPTS="-Djavax.net.ssl.trustStore=${TRUSTSTORE_PATH} -Djavax.net.ssl.trustStorePassword=changeit -Djavax.net.ssl.trustStoreType=PKCS12"
