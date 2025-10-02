package itmo.soa.service1.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class RequestLoggingFilter implements Filter {

  @Override
  public void doFilter(
    ServletRequest request,
    ServletResponse response,
    FilterChain chain
  ) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;

    System.out.println("Method: " + req.getMethod());
    System.out.println("URI: " + req.getRequestURI());
    if (req.getQueryString() != null) {
      System.out.println("Query: " + req.getQueryString());
    }

    chain.doFilter(request, response);
  }
}
