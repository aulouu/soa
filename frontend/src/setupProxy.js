const {createProxyMiddleware} = require("http-proxy-middleware");

const onProxyReq = function (proxyReq, req, res) {
    console.log("---");
    console.log(
        `[Proxy Log] Получен запрос от клиента: ${req.method} ${req.originalUrl}`
    );
    console.log(
        `[Proxy Log] Перенаправляю на -> ${proxyReq.getHeader("host")}${
            proxyReq.path
        }`
    );
    console.log("---");
};

module.exports = function (app) {
    app.use(
        "/api",
        createProxyMiddleware({
            target: "https://localhost:8443",
            changeOrigin: true,
            secure: false,
            onProxyReq,
            logLevel: "debug",
        })
    );

    app.use(
        "/s2",
        createProxyMiddleware({
            target: "https://localhost:8643",
            changeOrigin: true,
            secure: false,
            pathRewrite: {"^/s2": ""},
            onProxyReq,
            logLevel: "debug",
        })
    );
};
