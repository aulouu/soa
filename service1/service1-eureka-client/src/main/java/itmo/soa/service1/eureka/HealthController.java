package itmo.soa.service1.eureka;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

  @GetMapping("/health")
  public String health() {
    return "OK";
  }

  @GetMapping("/info")
  public String info() {
    return "Eureka Client for Human Beings Service (WildFly on 8082)";
  }
}
