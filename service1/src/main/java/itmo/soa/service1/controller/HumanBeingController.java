package itmo.soa.service1.controller;

import itmo.soa.service1.dto.HumanBeingRequest;
import itmo.soa.service1.dto.HumanBeingResponse;
import itmo.soa.service1.service.HumanBeingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/human-beings")
public class HumanBeingController {
    private final HumanBeingService humanBeingService;

    @GetMapping
    public Page<HumanBeingResponse> getAllHumanBeings(Pageable pageable) {
        return humanBeingService.getAllHumanBeings(pageable);
    }

    @GetMapping("/{id}")
    public HumanBeingResponse getHumanBeingById(@PathVariable Long id) {
        return humanBeingService.getHumanBeingById(id);
    }

    @PostMapping
    public HumanBeingResponse addHumanBeing(@RequestBody @Valid HumanBeingRequest humanBeingRequest) {
        return humanBeingService.addHumanBeing(humanBeingRequest);
    }

    @PutMapping("/{id}")
    public HumanBeingResponse updateHumanBeingById(@PathVariable Long id, @RequestBody @Valid HumanBeingRequest humanBeingRequest) {
        return humanBeingService.updateHumanBeingById(id, humanBeingRequest);
    }

    @DeleteMapping("/{id}")
    public void deleteHumanBeingById(@PathVariable Long id) {
        humanBeingService.deleteHumanBeingById(id);
    }
}
