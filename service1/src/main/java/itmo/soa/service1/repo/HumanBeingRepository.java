package itmo.soa.service1.repo;

import itmo.soa.service1.model.HumanBeing;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HumanBeingRepository
  extends
    JpaRepository<HumanBeing, Long>, JpaSpecificationExecutor<HumanBeing> {
  @Query("SELECT h FROM HumanBeing h WHERE h.name LIKE CONCAT(:substring, '%')")
  List<HumanBeing> findByNameStartingWith(String substring);

  @Query(
    "SELECT DISTINCT h.impactSpeed FROM HumanBeing h WHERE h.impactSpeed IS NOT NULL"
  )
  List<Integer> findDistinctImpactSpeeds();
}
