package itmo.soa.service1.controller;

import itmo.soa.service1.model.HumanBeing;
import itmo.soa.service1.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    @GetMapping("/mood-count/{mood}")
    public Long countByMoodLessThan(@PathVariable int mood) {
        return statisticsService.countByMoodLessThan(mood);
    }

    @GetMapping("/name/starts-with/{substring}")
    public List<HumanBeing> findByNameStartingWith(@PathVariable String substring) {
        return statisticsService.findByNameStartingWith(substring);
    }


    @GetMapping("/impact-speeds")
    public List<Integer> findDistinctImpactSpeeds() {
        return statisticsService.findDistinctImpactSpeeds();
    }
}
