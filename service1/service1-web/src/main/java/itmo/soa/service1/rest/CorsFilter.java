package itmo.soa.service1.rest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CorsFilter implements ContainerResponseFilter {

  @Override
  public void filter(
    ContainerRequestContext requestContext,
    ContainerResponseContext responseContext
  ) throws IOException {
    responseContext
      .getHeaders()
      .add("Access-Control-Allow-Origin", "*");
    responseContext
      .getHeaders()
      .add("Access-Control-Allow-Credentials", "false");
    responseContext
      .getHeaders()
      .add(
        "Access-Control-Allow-Headers",
        "Origin, X-Requested-With, Content-Type, Accept, Authorization, Cache-Control, X-CSRF-TOKEN"
      );
    responseContext
      .getHeaders()
      .add(
        "Access-Control-Allow-Methods",
        "GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD"
      );
    responseContext
      .getHeaders()
      .add("Access-Control-Max-Age", "3600");
    responseContext
      .getHeaders()
      .add("Access-Control-Expose-Headers", "*");
  }
}
