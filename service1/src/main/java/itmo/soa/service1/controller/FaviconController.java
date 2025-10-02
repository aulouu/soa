package itmo.soa.service1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class FaviconController {

  @RequestMapping("/favicon.ico")
  ResponseEntity<Void> favicon() {
    return ResponseEntity.noContent().build();
  }
}
