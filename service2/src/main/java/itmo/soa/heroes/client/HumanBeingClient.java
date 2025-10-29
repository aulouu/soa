package itmo.soa.heroes.client;

import itmo.soa.heroes.dto.HumanBeingRequest;
import itmo.soa.heroes.dto.HumanBeingResponse;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign клиент для вызова human-beings-service
 * Использует Ribbon для балансировки нагрузки
 * Сервис обнаруживается через Eureka/Consul
 */
@FeignClient(
  name = "human-beings-service",
  url = "http://localhost:8082/service1-web",
  path = "/api/human-beings"
)
public interface HumanBeingClient {
  @GetMapping
  Map<String, Object> getAllHumanBeings(
    @RequestParam(required = false) Long teamId,
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "100") int size
  );

  @GetMapping("/{id}")
  HumanBeingResponse getHumanBeingById(@PathVariable Long id);

  @PutMapping("/{id}")
  HumanBeingResponse updateHumanBeing(
    @PathVariable Long id,
    @RequestBody HumanBeingRequest request
  );

  @DeleteMapping("/{id}")
  void deleteHumanBeing(@PathVariable Long id);
}
