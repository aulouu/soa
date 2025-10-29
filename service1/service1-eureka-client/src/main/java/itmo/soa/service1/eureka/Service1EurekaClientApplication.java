package itmo.soa.service1.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class Service1EurekaClientApplication {

  public static void main(String[] args) {
    SpringApplication.run(Service1EurekaClientApplication.class, args);
  }
}
