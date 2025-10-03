package itmo.soa.service1.controller;

import itmo.soa.service1.dto.HumanBeingRequest;
import itmo.soa.service1.dto.HumanBeingResponse;
import itmo.soa.service1.model.HumanBeing;
import itmo.soa.service1.model.Mood;
import itmo.soa.service1.model.WeaponType;
import itmo.soa.service1.repo.HumanBeingSpecification;
import itmo.soa.service1.service.HumanBeingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/human-beings")
public class HumanBeingController {

    private final HumanBeingService humanBeingService;

    @GetMapping
    public Page<HumanBeingResponse> getAllHumanBeings(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean hasToothpick,
            @RequestParam(required = false) Boolean realHero,
            @RequestParam(required = false) Integer impactSpeed,
            @RequestParam(required = false) WeaponType weaponType,
            @RequestParam(required = false) Mood mood,
            @RequestParam(required = false) Integer teamId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAfter,
            Pageable pageable
    ) {
        Specification<HumanBeing> spec = Specification.where(null);

        if (id != null) spec = spec.and(HumanBeingSpecification.hasId(id));
        if (name != null) spec = spec.and(HumanBeingSpecification.nameContains(name));
        if (hasToothpick != null) spec = spec.and(HumanBeingSpecification.hasToothpick(hasToothpick));
        if (realHero != null) spec = spec.and(HumanBeingSpecification.realHero(realHero));
        if (impactSpeed != null) spec = spec.and(HumanBeingSpecification.impactSpeed(impactSpeed));
        if (weaponType != null) spec = spec.and(HumanBeingSpecification.weaponType(weaponType));
        if (mood != null) spec = spec.and(HumanBeingSpecification.mood(mood));
        if (teamId != null) spec = spec.and(HumanBeingSpecification.teamIdIs(teamId));
        if (createdAfter != null) spec = spec.and(HumanBeingSpecification.createdAfter(createdAfter));

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
