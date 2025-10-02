#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR=$(cd "$(dirname "$0")" && pwd)

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

# 2) Create service2 truststore with service1 cert (for HTTPS backend calls)
if [ ! -f "$ROOT_DIR/service2/ssl/truststore.p12" ]; then
  keytool -importcert -alias service1 -file "$ROOT_DIR/service1/ssl/service1.cer" \
    -keystore "$ROOT_DIR/service2/ssl/truststore.p12" -storetype PKCS12 \
    -storepass changeit -noprompt
fi

# 3) Build services
mvn -q -f "$ROOT_DIR/service1" -DskipTests clean package
mvn -q -f "$ROOT_DIR/service2" -DskipTests clean package

echo "Starting service1 (Spring Boot, HTTPS 8443) ..."
mkdir -p "$ROOT_DIR/logs"
(
  cd "$ROOT_DIR/service1" && \
  JAVA_TOOL_OPTIONS="-Duser.timezone=UTC" \
  mvn -q spring-boot:run | tee -a "$ROOT_DIR/logs/service1.log"
) &
sleep 8

echo "Starting service2 (Payara Micro JAR, HTTPS 8643, HTTP disabled) ..."
SERVICE2_WAR=$(ls -1 "$ROOT_DIR/service2/target"/*.war 2>/dev/null | head -n 1 || true)
if [ -z "$SERVICE2_WAR" ]; then
  echo "ERROR: service2 WAR was not found in service2/target. Did the build succeed?" >&2
  exit 1
fi

PAYARA_JAR="$ROOT_DIR/payara-micro-5.2022.5.jar"
if [ ! -f "$PAYARA_JAR" ]; then
  curl -L -o "$PAYARA_JAR" https://repo1.maven.org/maven2/fish/payara/extras/payara-micro/5.2022.5/payara-micro-5.2022.5.jar
fi

JAVA_OPTS="-Djavax.net.ssl.trustStore=$ROOT_DIR/service2/ssl/truststore.p12 -Djavax.net.ssl.trustStorePassword=changeit"
(
  java $JAVA_OPTS -jar "$PAYARA_JAR" \
    --deploy "$SERVICE2_WAR" \
    --contextRoot / \
    --sslPort 8643 \
    --nocluster | tee -a "$ROOT_DIR/logs/service2.log"
) &

echo "\nServices are starting..."
echo "service1: https://localhost:8443/api/human-beings"
echo "service2: https://localhost:8643/api/heroes"
echo "\nTailing logs (press Ctrl+C to stop)..."
tail -n +1 -f "$ROOT_DIR/logs/service1.log" "$ROOT_DIR/logs/service2.log"
wait


