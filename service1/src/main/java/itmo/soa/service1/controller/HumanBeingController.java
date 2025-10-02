package itmo.soa.service1.controller;

import itmo.soa.service1.dto.HumanBeingRequest;
import itmo.soa.service1.dto.HumanBeingResponse;
import itmo.soa.service1.model.HumanBeing;
import itmo.soa.service1.repo.HumanBeingSpecification;
import itmo.soa.service1.service.HumanBeingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/human-beings")
public class HumanBeingController {

  private final HumanBeingService humanBeingService;

  @GetMapping
  public Page<HumanBeingResponse> getAllHumanBeings(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) Boolean hasToothpick,
    @RequestParam(required = false) Boolean realHero,
    @RequestParam(required = false) Integer teamId,
    Pageable pageable
  ) {
    Specification<HumanBeing> spec = Specification.where(null);
    if (name != null) {
      spec = spec.and(HumanBeingSpecification.nameContains(name));
    }
    if (hasToothpick != null) {
      spec = spec.and(HumanBeingSpecification.hasToothpick(hasToothpick));
    }
    if (realHero != null) {
      spec = spec.and(HumanBeingSpecification.realHero(realHero));
    }

    if (teamId != null) {
      spec = spec.and(HumanBeingSpecification.teamIdIs(teamId));
    }
    return humanBeingService.getAllHumanBeings(spec, pageable);
  }

  @GetMapping("/{id}")
  public HumanBeingResponse getHumanBeingById(@PathVariable Long id) {
    return humanBeingService.getHumanBeingById(id);
  }

  @PostMapping
  public HumanBeingResponse addHumanBeing(
    @RequestBody @Valid HumanBeingRequest humanBeingRequest
  ) {
    return humanBeingService.addHumanBeing(humanBeingRequest);
  }

  @PutMapping("/{id}")
  public HumanBeingResponse updateHumanBeingById(
    @PathVariable Long id,
    @RequestBody @Valid HumanBeingRequest humanBeingRequest
  ) {
    return humanBeingService.updateHumanBeingById(id, humanBeingRequest);
  }

  @DeleteMapping("/{id}")
  public void deleteHumanBeingById(@PathVariable Long id) {
    humanBeingService.deleteHumanBeingById(id);
  }
}
