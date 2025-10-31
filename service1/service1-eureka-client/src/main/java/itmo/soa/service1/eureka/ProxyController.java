package itmo.soa.service1.eureka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Enumeration;

@RestController
@RequestMapping("/api/**")
public class ProxyController {

    @Value("${service1.wildfly.url:http://localhost:8082/service1-web}")
    private String wildflyUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH})
    public ResponseEntity<String> proxyRequest(
            HttpServletRequest request,
            @RequestBody(required = false) String body
    ) {
        try {
            // Получаем путь после /api/
            String path = request.getRequestURI();
            String queryString = request.getQueryString();
            
            // Формируем URL к WildFly
            String targetUrl = wildflyUrl + path;
            if (queryString != null && !queryString.isEmpty()) {
                targetUrl += "?" + queryString;
            }

            // Копируем заголовки
            HttpHeaders headers = new HttpHeaders();
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                // Пропускаем некоторые заголовки
                if (!headerName.equalsIgnoreCase("host") && 
                    !headerName.equalsIgnoreCase("content-length")) {
                    headers.set(headerName, request.getHeader(headerName));
                }
            }

            // Создаём запрос
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            
            // Отправляем запрос к WildFly
            ResponseEntity<String> response = restTemplate.exchange(
                    URI.create(targetUrl),
                    HttpMethod.valueOf(request.getMethod()),
                    entity,
                    String.class
            );

            return response;

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Proxy error: " + e.getMessage());
        }
    }
}
