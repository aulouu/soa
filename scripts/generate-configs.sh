#!/bin/bash
# Генерация конфигурационных файлов из config.env

set -e

# Загружаем конфигурацию
source ../config.env

echo "=== Generating Configuration Files ==="
echo ""

# Получаем абсолютный путь к проекту
PROJECT_DIR="$(cd .. && pwd)"

# 1. Config Server application.yml
cat > ../config-server/src/main/resources/application.yml << EOF
server:
  port: ${CONFIG_SERVER_PORT}
  # SSL отключен - не используется извне

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        native:
          search-locations: classpath:/config
  profiles:
    active: native

management:
  endpoints:
    web:
      exposure:
        include: health,info
EOF
echo "✓ Generated: config-server/src/main/resources/application.yml"

# 2. Eureka Server application.yml
cat > ../eureka-server/src/main/resources/application.yml << EOF
server:
  port: ${EUREKA_SERVER_PORT}
  # SSL отключен - внутренний сервис

spring:
  application:
    name: eureka-server

eureka:
  instance:
    hostname: ${EUREKA_HOST}
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: $(if [ "$SSL_ENABLED" = "true" ]; then echo "https"; else echo "http"; fi)://${EUREKA_HOST}:${EUREKA_SERVER_PORT}/eureka/
  server:
    enable-self-preservation: false
    eviction-interval-timer-in-ms: 10000
    response-cache-update-interval-ms: 5000

management:
  endpoints:
    web:
      exposure:
        include: health,info
EOF
echo "✓ Generated: eureka-server/src/main/resources/application.yml"

# 3. Zuul Gateway application.yml
cat > ../zuul-gateway/src/main/resources/application.yml << EOF
server:
  port: ${ZUUL_GATEWAY_PORT}
$(if [ "$SSL_ENABLED" = "true" ]; then
cat << SSLEOF
  ssl:
    enabled: true
    key-store: ${PROJECT_DIR}/ssl/keystore.p12
    key-store-password: ${SSL_KEY_STORE_PASSWORD}
    key-store-type: PKCS12
    key-alias: ${SSL_KEY_ALIAS}
SSLEOF
fi)

spring:
  application:
    name: zuul-gateway

eureka:
  client:
    service-url:
      defaultZone: http://${EUREKA_HOST}:${EUREKA_SERVER_PORT}/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true

zuul:
  routes:
    heroes-service:
      path: /api/heroes/**
      url: http://localhost:${SERVICE2_PORT}
      strip-prefix: false
    human-beings-service:
      path: /api/human-beings/**
      url: http://localhost:${SERVICE1_PORT}/service1-web
      strip-prefix: false
  host:
    connect-timeout-millis: 10000
    socket-timeout-millis: 60000
  add-proxy-headers: true

ribbon:
  eureka:
    enabled: false
  ConnectTimeout: 10000
  ReadTimeout: 60000

management:
  endpoints:
    web:
      exposure:
        include: health,info,routes
EOF
echo "✓ Generated: zuul-gateway/src/main/resources/application.yml"

# 4. Service2 (Spring Cloud) application.yml
cat > ../service2/src/main/resources/application.yml << EOF
server:
  port: ${SERVICE2_PORT}
  # SSL отключен - termination на Gateway

spring:
  application:
    name: heroes-service

eureka:
  client:
    service-url:
      defaultZone: http://${EUREKA_HOST}:${EUREKA_SERVER_PORT}/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30

ribbon:
  eureka:
    enabled: true
  ConnectTimeout: 10000
  ReadTimeout: 60000
  MaxAutoRetries: 1
  MaxAutoRetriesNextServer: 1
  ServerListRefreshInterval: 30000

feign:
  client:
    config:
      default:
        connectTimeout: 10000
        readTimeout: 60000
        loggerLevel: basic

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics

logging:
  level:
    itmo.soa.heroes: INFO
    org.springframework.cloud: WARN
EOF
echo "✓ Generated: service2/src/main/resources/application.yml"

# 5. Frontend environment config
cat > ../frontend/.env << EOF
# Generated from config.env
REACT_APP_API_URL=$(if [ "$SSL_ENABLED" = "true" ]; then echo "https"; else echo "http"; fi)://localhost:${ZUUL_GATEWAY_PORT}
REACT_APP_HUMAN_BEINGS_API=$(if [ "$SSL_ENABLED" = "true" ]; then echo "https"; else echo "http"; fi)://localhost:${ZUUL_GATEWAY_PORT}/api/human-beings
REACT_APP_HEROES_API=$(if [ "$SSL_ENABLED" = "true" ]; then echo "https"; else echo "http"; fi)://localhost:${ZUUL_GATEWAY_PORT}/api/heroes
EOF
echo "✓ Generated: frontend/.env"

# 6. Service1 environment variables script
cat > ../service1/set-env.sh << 'EOF'
#!/bin/bash
# Environment variables for Service1 (Jakarta EE)

# Load from config.env
source ../config.env

export SERVICE_PORT=${SERVICE1_PORT}
export CONSUL_HOST=${CONSUL_HOST}
export CONSUL_PORT=${CONSUL_PORT}
export DB_HOST=${DB_HOST}
export DB_PORT=${DB_PORT}
export DB_NAME=${DB_NAME}
export DB_USER=${DB_USER}
export DB_PASSWORD=${DB_PASSWORD}

echo "Environment variables set for Service1"
EOF
chmod +x ../service1/set-env.sh
echo "✓ Generated: service1/set-env.sh"

# Обновляем конфигурацию фронтенда
./update-frontend-config.sh

echo ""
echo "=== Configuration Files Generated Successfully ==="
echo ""
echo "All services will use settings from config.env:"
echo "  • Consul:          http://localhost:${CONSUL_PORT}"
echo "  • Config Server:   http://localhost:${CONFIG_SERVER_PORT} (internal)"
echo "  • Eureka:          http://localhost:${EUREKA_SERVER_PORT} (internal)"
echo "  • Zuul Gateway:    https://localhost:${ZUUL_GATEWAY_PORT} (public HTTPS)"
echo "  • Service1 (HB):   http://localhost:${SERVICE1_PORT} (internal)"
echo "  • Service2 (Hero): http://localhost:${SERVICE2_PORT} (internal)"
echo ""
echo "SSL Termination: Gateway handles HTTPS, internal services use HTTP"
echo ""
