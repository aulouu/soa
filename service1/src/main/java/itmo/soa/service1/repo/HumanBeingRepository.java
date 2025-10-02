package itmo.soa.service1.repo;

import itmo.soa.service1.model.HumanBeing;
import itmo.soa.service1.model.Mood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HumanBeingRepository extends JpaRepository<HumanBeing, Long> {
    // Вернуть количество объектов, значение поля mood которых меньше заданного
    @Query("SELECT COUNT(h) FROM HumanBeing h WHERE h.mood < :mood")
    Long countByMoodLessThan(Mood mood);

    // Вернуть массив объектов, значение поля name которых начинается с заданной подстроки
    @Query("SELECT h FROM HumanBeing h WHERE h.name LIKE CONCAT(:substring, '%')")
    List<HumanBeing> findByNameStartingWith(String substring);

    // Вернуть массив уникальных значений поля impactSpeed по всем объектам
    @Query("SELECT DISTINCT h.impactSpeed FROM HumanBeing h WHERE h.impactSpeed IS NOT NULL")
    List<Integer> findDistinctImpactSpeeds();
}
