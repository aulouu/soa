package itmo.soa.heroes.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * CORS конфигурация для Service2 (Heroes Service)
 * Разрешает запросы от фронтенда на localhost:3000
 */
@Configuration
public class CorsConfig {

  @Bean
  public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
    UrlBasedCorsConfigurationSource source =
      new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    // Разрешаем все источники для работы через Cloudflare Tunnel и локальной разработки
    // ВАЖНО: когда allowCredentials=false, можно использовать "*" для origins
    config.setAllowCredentials(false);
    config.setAllowedOrigins(Arrays.asList("*"));
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS", "HEAD"));
    config.setMaxAge(3600L);
    config.setExposedHeaders(Arrays.asList("*"));

    source.registerCorsConfiguration("/**", config);
    
    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return bean;
  }
}
