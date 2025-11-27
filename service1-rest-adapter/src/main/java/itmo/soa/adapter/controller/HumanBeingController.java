package itmo.soa.adapter.controller;

import itmo.soa.adapter.client.SoapClientService;
import itmo.soa.adapter.model.HumanBeingDTO;
import itmo.soa.adapter.model.PagedResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * REST контроллер - прослойка между REST API и SOAP сервисом
 * Полностью совместим с API Service1
 */
@RestController
@RequestMapping("/api/human-beings")
@CrossOrigin(origins = "*")
public class HumanBeingController {

    private static final Logger log = Logger.getLogger(HumanBeingController.class.getName());

    private final SoapClientService soapClient;

    @Autowired
    public HumanBeingController(SoapClientService soapClient) {
        this.soapClient = soapClient;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllHumanBeings(
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
        
        // log.info("REST getAllHumanBeings called, forwarding to SOAP");
        
        PagedResponseDTO response = soapClient.getAllHumanBeings(
            id, name, hasToothpick, realHero, impactSpeed, 
            weaponType, mood, teamId, createdAfter, page, size
        );
        
        // Преобразуем в формат который ожидает фронтенд
        Map<String, Object> result = new HashMap<>();
        result.put("content", response.getContent());
        result.put("totalElements", response.getTotalElements());
        result.put("totalPages", response.getTotalPages());
        result.put("size", response.getSize());
        result.put("number", response.getNumber());
        result.put("first", response.isFirst());
        result.put("last", response.isLast());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HumanBeingDTO> getHumanBeingById(@PathVariable Long id) {
        // log.info("REST getHumanBeingById called with id: {}, forwarding to SOAP", id);
        HumanBeingDTO dto = soapClient.getHumanBeingById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<HumanBeingDTO> createHumanBeing(@RequestBody HumanBeingDTO dto) {
        // log.info("REST createHumanBeing called, forwarding to SOAP");
        HumanBeingDTO created = soapClient.createHumanBeing(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HumanBeingDTO> updateHumanBeing(
            @PathVariable Long id,
            @RequestBody HumanBeingDTO dto) {
        // log.info("REST updateHumanBeing called with id: {}, forwarding to SOAP", id);
        HumanBeingDTO updated = soapClient.updateHumanBeing(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHumanBeing(@PathVariable Long id) {
        // log.info("REST deleteHumanBeing called with id: {}, forwarding to SOAP", id);
        soapClient.deleteHumanBeing(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics/mood-count/{moodValue}")
    public ResponseEntity<Map<String, Object>> countByMood(@PathVariable int moodValue) {
        // log.info("REST countByMood called with value: {}, forwarding to SOAP", moodValue);
        long count = soapClient.countByMood(moodValue);
        
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("mood", convertMoodValue(moodValue));
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/statistics/impact-speeds")
    public ResponseEntity<List<Integer>> getUniqueImpactSpeeds() {
        // log.info("REST getUniqueImpactSpeeds called, forwarding to SOAP");
        List<Integer> speeds = soapClient.getUniqueImpactSpeeds();
        return ResponseEntity.ok(speeds);
    }

    @GetMapping("/statistics/name/starts-with/{prefix}")
    public ResponseEntity<Map<String, Object>> countByNameStartsWith(@PathVariable String prefix) {
        // log.info("REST countByNameStartsWith called with prefix: {}, forwarding to SOAP", prefix);
        long count = soapClient.countByNameStartsWith(prefix);
        
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        result.put("prefix", prefix);
        
        return ResponseEntity.ok(result);
    }

    private String convertMoodValue(int value) {
        switch (value) {
            case 0: return "SADNESS";
            case 1: return "SORROW";
            case 2: return "APATHY";
            case 3: return "FRENZY";
            default: return "UNKNOWN";
        }
    }
}
