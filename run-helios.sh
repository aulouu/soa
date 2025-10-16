#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR=$(cd "$(dirname "$0")" && pwd)
LOG_DIR="$ROOT_DIR/logs"
TMP_DIR="/var/tmp/soa_$USER"

echo "Using temporary directory: $TMP_DIR"
mkdir -p "$LOG_DIR" "$TMP_DIR"

SPRING_PORT=8449
PAYARA_PORT=8448

# Функция для освобождения порта
free_port() {
    local PORT=$1
    local PID
    PID=$(lsof -ti tcp:$PORT || true)
    if [ -n "$PID" ]; then
        echo "Port $PORT is in use by PID $PID. Killing..."
        kill -9 $PID
        sleep 2
    fi
}

# Освобождаем порты
free_port $SPRING_PORT
free_port $PAYARA_PORT

# 1) Generate SSL artifacts if missing
mkdir -p "$ROOT_DIR/service1/ssl" "$ROOT_DIR/service2/ssl"

if [ ! -f "$ROOT_DIR/service1/ssl/service1.p12" ]; then
  keytool -genkeypair -alias service1 -keyalg RSA -keysize 2048 \
    -storetype PKCS12 -keystore "$ROOT_DIR/service1/ssl/service1.p12" \
    -storepass changeit -keypass changeit -validity 365 -dname "CN=localhost"
  keytool -export -alias service1 -keystore "$ROOT_DIR/service1/ssl/service1.p12" \
    -storepass changeit -file "$ROOT_DIR/service1/ssl/service1.cer" -rfc
fi

if [ ! -f "$ROOT_DIR/service2/ssl/service2.p12" ]; then
  keytool -genkeypair -alias service2 -keyalg RSA -keysize 2048 \
    -storetype PKCS12 -keystore "$ROOT_DIR/service2/ssl/service2.p12" \
    -storepass changeit -keypass changeit -validity 365 -dname "CN=localhost"
fi

# 2) Create service2 truststore with service1 cert
if [ ! -f "$ROOT_DIR/service2/ssl/truststore.p12" ]; then
  keytool -importcert -alias service1 -file "$ROOT_DIR/service1/ssl/service1.cer" \
    -keystore "$ROOT_DIR/service2/ssl/truststore.p12" -storetype PKCS12 \
    -storepass changeit -noprompt
fi

# 3) Build services using local Maven repo in TMP_DIR
export MAVEN_OPTS="-Dmaven.repo.local=$TMP_DIR/m2repo"
mvn -q -f "$ROOT_DIR/service1" -DskipTests clean package
mvn -q -f "$ROOT_DIR/service2" -DskipTests clean package

# 4) Start service1 (Spring Boot)
echo "Starting service1 (Spring Boot, HTTPS $SPRING_PORT) ..."
(
  cd "$ROOT_DIR/service1" && \
  JAVA_TOOL_OPTIONS="-Duser.timezone=UTC -Djava.io.tmpdir=$TMP_DIR" \
  mvn -q spring-boot:run -Dserver.port=$SPRING_PORT | tee -a "$LOG_DIR/service1.log"
) &

sleep 8

# 5) Start service2 (Payara Micro)
echo "Starting service2 (Payara Micro, HTTPS $PAYARA_PORT) ..."
SERVICE2_WAR=$(ls -1 "$ROOT_DIR/service2/target"/*.war 2>/dev/null | head -n 1 || true)
if [ -z "$SERVICE2_WAR" ]; then
  echo "ERROR: service2 WAR was not found. Build failed?" >&2
  exit 1
fi

PAYARA_JAR="$ROOT_DIR/payara-micro-5.2022.5.jar"
if [ ! -f "$PAYARA_JAR" ]; then
  curl -L -o "$PAYARA_JAR" https://repo1.maven.org/maven2/fish/payara/extras/payara-micro/5.2022.5/payara-micro-5.2022.5.jar
fi

JAVA_OPTS="-Djavax.net.ssl.trustStore=$ROOT_DIR/service2/ssl/truststore.p12 -Djavax.net.ssl.trustStorePassword=changeit -Djava.io.tmpdir=$TMP_DIR"
(
  java $JAVA_OPTS -jar "$PAYARA_JAR" \
    --deploy "$SERVICE2_WAR" \
    --contextRoot / \
    --sslPort $PAYARA_PORT \
    --nocluster | tee -a "$LOG_DIR/service2.log"
) &

echo -e "\nServices are starting..."
echo "service1: https://se.ifmo.ru:$SPRING_PORT/api/human-beings"
echo "service2: https://se.ifmo.ru:$PAYARA_PORT/api/heroes"
echo -e "\nTailing logs (Ctrl+C to stop)..."
tail -n +1 -f "$LOG_DIR/service1.log" "$LOG_DIR/service2.log"

wait
