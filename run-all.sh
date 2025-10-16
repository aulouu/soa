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

# 4) Create payara-config.yaml with Hazelcast and all unnecessary services disabled
cat > "$ROOT_DIR/payara-config.yaml" <<EOF
set configs.config.server-config.hazelcast-config-specific-configuration.clustering-enabled=false
set configs.config.server-config.hazelcast-config-specific-configuration.enabled=false
set configs.config.server-config.network-config.cache-enabled=false
set configs.config.server-config.ejb-container.property.disable-nonstandard-wrappers=true
set configs.config.server-config.ejb-container.property.disable-auditing=true
set configs.config.server-config.jms-service.type=LOCAL
set configs.config.server-config.jmx-service.jmx-connector.system.enabled=false
set configs.config.server-config.monitoring-service.enabled=false
set configs.config.server-config.request-tracing-service.enabled=false
set configs.config.server-config.microprofile-fault-tolerance-service.enabled=false
EOF

echo "Starting service1 (Spring Boot, HTTPS 37449) with minimal resources..."
mkdir -p "$ROOT_DIR/logs"
(
  cd "$ROOT_DIR/service1" && \
  export JAVA_TOOL_OPTIONS="-Duser.timezone=UTC -Xmx256m -Xms128m -XX:MaxMetaspaceSize=64m -Xss256k" && \
  export MAVEN_OPTS="-Xmx256m -Xms128m -XX:MaxMetaspaceSize=64m -Xss256k" && \
  mvn -q spring-boot:run | tee -a "$ROOT_DIR/logs/service1.log"
) &
SERVICE1_PID=$!
sleep 30  # Даём время на запуск Spring Boot

echo "Starting service2 (Payara Micro, HTTPS 37672) with Hazelcast and all unnecessary services disabled..."
SERVICE2_WAR=$(ls -1 "$ROOT_DIR/service2/target"/*.war 2>/dev/null | head -n 1 || true)
if [ -z "$SERVICE2_WAR" ]; then
  echo "ERROR: service2 WAR was not found in service2/target. Did the build succeed?" >&2
  exit 1
fi
PAYARA_JAR="$ROOT_DIR/payara-micro-5.2022.5.jar"
if [ ! -f "$PAYARA_JAR" ]; then
  curl -L -o "$PAYARA_JAR" https://repo1.maven.org/maven2/fish/payara/extras/payara-micro/5.2022.5/payara-micro-5.2022.5.jar
fi

# Минимальные настройки для Payara Micro с отключённым Hazelcast и всеми ненужными сервисами
PAYARA_OPTS="\
  -Djavax.net.ssl.trustStore=$ROOT_DIR/service2/ssl/truststore.p12 \
  -Djavax.net.ssl.trustStorePassword=changeit \
  -Xmx256m -Xms128m -XX:MaxMetaspaceSize=64m -Xss256k \
  -Dorg.jboss.weld.construction.disabled=true \
  -Dorg.jboss.weld.bootstrap.disabled=true \
  -Dorg.jboss.weld.version=0 \
  -Dorg.jboss.weld.verbose=false \
  -Dfish.payara.nucleus.threadpool.max-pool-size=1 \
  -Dfish.payara.nucleus.threadpool.min-pool-size=1 \
  -Dhazelcast.pool.size=0 \
  -Dhazelcast.shutdownhook.enabled=false \
  -Dhazelcast.shutdownhook.policy=TERMINATE \
  -Djava.util.concurrent.ForkJoinPool.common.parallelism=1 \
  -Dfish.payara.enterprise.concurrent.ManagedThreadFactory.max-pool-size=1 \
  -Dfish.payara.enterprise.concurrent.ManagedExecutorService.max-pool-size=1 \
  -Dfish.payara.nucleus.grizzly.max-thread-pool-size=1 \
  -Dfish.payara.nucleus.grizzly.min-thread-pool-size=1 \
  -Dfish.payara.nucleus.grizzly.max-queued-requests=5 \
  -Dfish.payara.nucleus.grizzly.thread-queue-capacity=5"

(
  java $PAYARA_OPTS -jar "$PAYARA_JAR" \
    --deploy "$SERVICE2_WAR" \
    --contextRoot / \
    --port 37812 \
    --sslPort 37672 \
    --prebootcommandfile "$ROOT_DIR/payara-config.yaml" \
    --nocluster
) &
SERVICE2_PID=$!

echo "\nServices are starting with minimal resources..."
echo "service1: https://localhost:37449/api/human-beings"
echo "service2: https://localhost:37672/api/heroes"
echo "\nTailing logs (press Ctrl+C to stop)..."
tail -n +1 -f "$ROOT_DIR/logs/service1.log" "$ROOT_DIR/logs/service2.log"

wait

