window.onload = function() {
  //<editor-fold desc="Changeable Configuration Block">

  // Swagger UI
  window.ui = SwaggerUIBundle({
    urls: [
      { url: "../Service1.yaml", name: "Service1" },
      { url: "../Service2.yaml", name: "Service2" },
    ],
    dom_id: '#swagger-ui',
    deepLinking: true,
    presets: [
      SwaggerUIBundle.presets.apis,
      SwaggerUIStandalonePreset
    ],
    plugins: [
      SwaggerUIBundle.plugins.DownloadUrl
    ],
    layout: "StandaloneLayout"
  });

  //</editor-fold>
};
