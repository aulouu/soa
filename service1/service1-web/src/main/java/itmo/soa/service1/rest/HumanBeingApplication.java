package itmo.soa.service1.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class HumanBeingApplication extends Application {
    // JAX-RS будет автоматически сканировать классы с аннотациями @Path
}
