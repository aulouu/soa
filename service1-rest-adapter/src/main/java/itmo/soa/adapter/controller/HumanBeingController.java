package itmo.soa.adapter.rest.controller;

import itmo.soa.adapter.rest.HumanBeingSoapClient;
import itmo.soa.adapter.soap.generated.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/human-beings")
public class HumanBeingController {

    private final HumanBeingSoapClient soapClient;

    public HumanBeingController() {
        this.soapClient = new HumanBeingSoapClient();
    }

    @GetMapping
    public PagedResponse getAllHumanBeings(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean hasToothpick,
            @RequestParam(required = false) Boolean realHero,
            @RequestParam(required = false) Integer impactSpeed,
            @RequestParam(required = false) String weaponType,
            @RequestParam(required = false) String mood,
            @RequestParam(required = false) Integer teamId,
            @RequestParam(required = false) String createdAfter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return soapClient.getService().getAllHumanBeings(
                id, name, hasToothpick, realHero, impactSpeed,
                weaponType, mood, teamId, createdAfter, page, size
        );
    }

    @GetMapping("/{id}")
    public HumanBeingDTO getHumanBeingById(@PathVariable Long id) {
        return soapClient.getService().getHumanBeingById(id);
    }

    @PostMapping
    public HumanBeingDTO createHumanBeing(@RequestBody HumanBeingDTO humanBeingDTO) {
        return soapClient.getService().createHumanBeing(humanBeingDTO);
    }

    @PutMapping("/{id}")
    public HumanBeingDTO updateHumanBeing(@PathVariable Long id,
                                          @RequestBody HumanBeingDTO humanBeingDTO) {
        return soapClient.getService().updateHumanBeing(id, humanBeingDTO);
    }

    @DeleteMapping("/{id}")
    public void deleteHumanBeing(@PathVariable Long id) {
        soapClient.getService().deleteHumanBeing(id);
    }

    @GetMapping("/statistics/mood-count/{moodValue}")
    public long countByMood(@PathVariable int moodValue) {
        return soapClient.getService().countByMood(moodValue);
    }

    @GetMapping("/statistics/name/starts-with/{prefix}")
    public long countByNameStartsWith(@PathVariable String prefix) {
        return soapClient.getService().countByNameStartsWith(prefix);
    }

    @GetMapping("/statistics/impact-speeds")
    public List<Integer> getUniqueImpactSpeeds() {
        return soapClient.getService().getUniqueImpactSpeeds();
    }
}
