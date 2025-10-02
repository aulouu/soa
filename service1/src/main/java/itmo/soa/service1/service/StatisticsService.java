package itmo.soa.service1.service;

import itmo.soa.service1.exception.InvalidRequestException;
import itmo.soa.service1.model.HumanBeing;
import itmo.soa.service1.model.Mood;
import itmo.soa.service1.repo.HumanBeingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StatisticsService {
    private final HumanBeingRepository humanBeingRepository;

    public Long countByMoodLessThan(int moodIndex) {
        if (moodIndex < 0 || moodIndex >= Mood.values().length) {
            throw new InvalidRequestException("Invalid mood index. Must be 0, 1, 2, 3");
        }
        Mood mood = Mood.values()[moodIndex];
        return humanBeingRepository.findAll().stream()
                .filter(h -> h.getMood() != null && h.getMood().ordinal() < mood.ordinal())
                .count();
    }

    public List<HumanBeing> findByNameStartingWith(String substring) {
        if (substring == null || substring.isEmpty()) {
            throw new InvalidRequestException("Substring must be provided");
        }
        return humanBeingRepository.findByNameStartingWith(substring);
    }

    public List<Integer> findDistinctImpactSpeeds() {
        return humanBeingRepository.findDistinctImpactSpeeds();
    }
}
