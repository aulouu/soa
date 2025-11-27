package itmo.soa.heroes.client;

import itmo.soa.heroes.dto.HumanBeingRequest;
import itmo.soa.heroes.dto.HumanBeingResponse;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign клиент для вызова human-beings-service
 * ОБНОВЛЕНО ДЛЯ LAB4: Теперь вызывает Mule ESB вместо прямого вызова Service1
 * Mule ESB (порт 8081) играет роль интеграционной шины:
 * Service2 -> Mule ESB (:8081) -> REST-adapter (:9090) -> SOAP Service1 (:8082)
 */
@FeignClient(
  name = "human-beings-service",
  url = "http://localhost:8081",
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
