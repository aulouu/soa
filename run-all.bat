@echo off
setlocal enabledelayedexpansion

REM ===== ROOT DIR =====
set ROOT_DIR=%~dp0
set ROOT_DIR=%ROOT_DIR:~0,-1%

REM ===== Create SSL folders =====
if not exist "%ROOT_DIR%\service1\ssl" mkdir "%ROOT_DIR%\service1\ssl"
if not exist "%ROOT_DIR%\service2\ssl" mkdir "%ROOT_DIR%\service2\ssl"

REM ===== Generate service1 cert =====
if not exist "%ROOT_DIR%\service1\ssl\service1.p12" (
  keytool -genkeypair -alias service1 -keyalg RSA -keysize 2048 ^
    -storetype PKCS12 -keystore "%ROOT_DIR%\service1\ssl\service1.p12" ^
    -storepass changeit -keypass changeit -validity 365 -dname "CN=localhost"
  keytool -export -alias service1 -keystore "%ROOT_DIR%\service1\ssl\service1.p12" ^
    -storepass changeit -file "%ROOT_DIR%\service1\ssl\service1.cer" -rfc
)

REM ===== Generate service2 cert =====
if not exist "%ROOT_DIR%\service2\ssl\service2.p12" (
  keytool -genkeypair -alias service2 -keyalg RSA -keysize 2048 ^
    -storetype PKCS12 -keystore "%ROOT_DIR%\service2\ssl\service2.p12" ^
    -storepass changeit -keypass changeit -validity 365 -dname "CN=localhost"
)

REM ===== Create truststore =====
if not exist "%ROOT_DIR%\service2\ssl\truststore.p12" (
  keytool -importcert -alias service1 -file "%ROOT_DIR%\service1\ssl\service1.cer" ^
    -keystore "%ROOT_DIR%\service2\ssl\truststore.p12" -storetype PKCS12 ^
    -storepass changeit -noprompt
)

REM ===== Build services =====
call mvn -q -f "%ROOT_DIR%\service1" -DskipTests clean package
call mvn -q -f "%ROOT_DIR%\service2" -DskipTests clean package

REM ===== Logs =====
if not exist "%ROOT_DIR%\logs" mkdir "%ROOT_DIR%\logs"

echo Starting service1 (Spring Boot, HTTPS 8449)...
start "service1" cmd /c "cd /d %ROOT_DIR%\service1 && mvn -q spring-boot:run > %ROOT_DIR%\logs\service1.log 2>&1"

timeout /t 8 > nul

echo Starting service2 (Payara Micro JAR, HTTPS 8672)...
for %%f in ("%ROOT_DIR%\service2\target\*.war") do set SERVICE2_WAR=%%f

if not exist "%SERVICE2_WAR%" (
  echo ERROR: service2 WAR not found.
  exit /b 1
)

set PAYARA_JAR=%ROOT_DIR%\payara-micro-5.2022.5.jar
if not exist "%PAYARA_JAR%" (
  powershell -Command "Invoke-WebRequest -Uri https://repo1.maven.org/maven2/fish/payara/extras/payara-micro/5.2022.5/payara-micro-5.2022.5.jar -OutFile '%PAYARA_JAR%'"
)

set JAVA_OPTS=-Djavax.net.ssl.trustStore=%ROOT_DIR%\service2\ssl\truststore.p12 -Djavax.net.ssl.trustStorePassword=changeit
start "service2" cmd /c "java %JAVA_OPTS% -jar %PAYARA_JAR% --deploy %SERVICE2_WAR% --contextRoot / --sslPort 8672 --nocluster > %ROOT_DIR%\logs\service2.log 2>&1"

echo.
echo Services are starting...
echo service1: https://localhost:8449/api/human-beings
echo service2: https://localhost:8672/api/heroes
echo.
echo Logs: %ROOT_DIR%\logs
pause
