package itmo.soa.service1.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
@Startup
public class ConsulServiceRegistry {

  private static final Logger LOGGER = Logger.getLogger(
    ConsulServiceRegistry.class.getName()
  );

  private static final String SERVICE_NAME = "human-beings-service";
  private static final String CONSUL_HOST = System.getenv()
    .getOrDefault("CONSUL_HOST", "localhost");
  private static final int CONSUL_PORT = Integer.parseInt(
    System.getenv().getOrDefault("CONSUL_PORT", "8500")
  );
  private static final int SERVICE_PORT = Integer.parseInt(
    System.getenv().getOrDefault("SERVICE_PORT", "8082")
  );

  private ConsulClient consulClient;
  private String serviceId;

  @PostConstruct
  public void registerService() {
    try {
      LOGGER.info("Connecting to Consul at " + CONSUL_HOST + ":" + CONSUL_PORT);

      consulClient = new ConsulClient(CONSUL_HOST, CONSUL_PORT);

      String hostAddress = InetAddress.getLocalHost().getHostAddress();
      serviceId = SERVICE_NAME + "-" + hostAddress + "-" + SERVICE_PORT;

      NewService service = new NewService();
      service.setId(serviceId);
      service.setName(SERVICE_NAME);
      service.setAddress(hostAddress);
      service.setPort(SERVICE_PORT);

      // Health check
      NewService.Check check = new NewService.Check();
      check.setHttp(
        "http://" +
        hostAddress +
        ":" +
        SERVICE_PORT +
        "/service1-web/api/health" // Обновил путь для JAX-RS
      );
      check.setInterval("10s");
      service.setCheck(check);

      consulClient.agentServiceRegister(service);
      LOGGER.info("Service registered in Consul with ID: " + serviceId);
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Failed to register service in Consul", e);
      // Не падаем, если Consul недоступен
    }
  }

  @PreDestroy
  public void deregisterService() {
    if (consulClient != null && serviceId != null) {
      try {
        consulClient.agentServiceDeregister(serviceId);
        LOGGER.info("Service deregistered from Consul: " + serviceId);
      } catch (Exception e) {
        LOGGER.log(
          Level.WARNING,
          "Failed to deregister service from Consul",
          e
        );
      }
    }
  }
}
