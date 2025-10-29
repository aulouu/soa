package itmo.soa.heroes.controller;

import itmo.soa.heroes.dto.HumanBeingResponse;
import itmo.soa.heroes.service.HeroesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST контроллер для работы с героями
 * Сохраняет совместимость с предыдущим API
 */
@RestController
@RequestMapping("/api/heroes")
@RequiredArgsConstructor
@Slf4j
public class HeroesController {

    private final HeroesService heroesService;

    @GetMapping
    public ResponseEntity<List<HumanBeingResponse>> getAllHeroes() {
        log.info("GET /api/heroes - get all heroes");
        return ResponseEntity.ok(heroesService.getAllHeroes());
    }

    @DeleteMapping("/team/{teamId}/remove-without-toothpick")
    public ResponseEntity<Void> removeWithoutToothpick(@PathVariable Long teamId) {
        log.info("DELETE /api/heroes/team/{}/remove-without-toothpick", teamId);
        heroesService.removeWithoutToothpick(teamId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/team/{teamId}/car/add")
    public ResponseEntity<List<HumanBeingResponse>> addCarToTeam(@PathVariable Long teamId) {
        log.info("POST /api/heroes/team/{}/car/add", teamId);
        List<HumanBeingResponse> updated = heroesService.addCarToTeam(teamId);
        return ResponseEntity.ok(updated);
    }
}
