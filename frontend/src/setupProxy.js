const { createProxyMiddleware } = require("http-proxy-middleware");

// Отключаем проверку SSL сертификатов (для самоподписанных сертификатов)
const httpsAgent = new (require('https').Agent)({
    rejectUnauthorized: false  // Игнорировать SSL ошибки
});

module.exports = function (app) {

    // 1. Все запросы к /api → Zuul Gateway (HTTPS, порт 8080)
    app.use(
        "/api",
        createProxyMiddleware({
            target: "https://localhost:8080",  // HTTPS!
            changeOrigin: true,
            secure: false,  // false для игнорирования SSL ошибок
            agent: httpsAgent,  // Используем кастомный https agent
            logLevel: "debug",
            onProxyReq: (proxyReq, req, res) => {
                console.log(`[Proxy] ${req.method} ${req.originalUrl} → ${proxyReq.getHeader("host")}${proxyReq.path}`);
            },
            onError: (err, req, res) => {
                console.error('[Proxy Error]', err);
                res.status(500).json({ error: 'Proxy error', message: err.message });
            }
        })
    );

    // 2. Для прямого доступа к REST Adapter (опционально)
    app.use(
        "/rest-adapter",
        createProxyMiddleware({
            target: "https://localhost:9090",  // HTTPS!
            changeOrigin: true,
            secure: false,
            agent: httpsAgent,
            pathRewrite: { "^/rest-adapter": "" },
            logLevel: "debug",
        })
    );

    // 3. Для прямого доступа к Service2 (опционально)
    app.use(
        "/service2",
        createProxyMiddleware({
            target: "https://localhost:8091",  // HTTPS!
            changeOrigin: true,
            secure: false,
            agent: httpsAgent,
            pathRewrite: { "^/service2": "" },
            logLevel: "debug",
        })
    );
};
