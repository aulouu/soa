package itmo.soa.service1.ejb;

import itmo.soa.service1.model.HumanBeing;
import itmo.soa.service1.model.Mood;
import itmo.soa.service1.model.WeaponType;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stateless EJB с бизнес-логикой для работы с HumanBeing
 * Настройка пула производится через payara-config.yaml
 */
@Stateless
public class HumanBeingServiceBean implements HumanBeingServiceLocal {

  private static final Logger logger = Logger.getLogger(
    HumanBeingServiceBean.class.getName()
  );

  @PersistenceContext(unitName = "HumanBeingPU")
  private EntityManager entityManager;

  public HumanBeingServiceBean() {
    // Лог при создании экземпляра
    logger.info("HumanBeingServiceBean INSTANCE CREATED.");
  }

  @Override
  public Map<String, Object> getAllHumanBeings(
    Long id,
    String name,
    Boolean hasToothpick,
    Boolean realHero,
    Integer impactSpeed,
    WeaponType weaponType,
    Mood mood,
    Integer teamId,
    LocalDateTime createdAfter,
    int page,
    int size
  ) {
    logger.log(
      Level.INFO,
      "getAllHumanBeings called with page={0}, size={1}",
      new Object[] { page, size }
    );

    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<HumanBeing> query = cb.createQuery(HumanBeing.class);
    Root<HumanBeing> root = query.from(HumanBeing.class);

    List<Predicate> predicates = new ArrayList<>();

    if (id != null) {
      predicates.add(cb.equal(root.get("id"), id));
    }
    if (name != null && !name.isEmpty()) {
      predicates.add(
        cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%")
      );
    }
    if (hasToothpick != null) {
      predicates.add(cb.equal(root.get("hasToothpick"), hasToothpick));
    }
    if (realHero != null) {
      predicates.add(cb.equal(root.get("realHero"), realHero));
    }
    if (impactSpeed != null) {
      predicates.add(cb.equal(root.get("impactSpeed"), impactSpeed));
    }
    if (weaponType != null) {
      predicates.add(cb.equal(root.get("weaponType"), weaponType));
    }
    if (mood != null) {
      predicates.add(cb.equal(root.get("mood"), mood));
    }
    if (teamId != null) {
      predicates.add(cb.equal(root.get("teamId"), teamId));
    }
    if (createdAfter != null) {
      predicates.add(cb.greaterThan(root.get("creationDate"), createdAfter));
    }

    if (!predicates.isEmpty()) {
      query.where(predicates.toArray(new Predicate[0]));
    }

    // Подсчет общего количества
    CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
    Root<HumanBeing> countRoot = countQuery.from(HumanBeing.class);
    countQuery.select(cb.count(countRoot));
    if (!predicates.isEmpty()) {
      // Пересоздаем предикаты для count запроса
      countQuery.where(
        createPredicates(
          cb,
          countRoot,
          id,
          name,
          hasToothpick,
          realHero,
          impactSpeed,
          weaponType,
          mood,
          teamId,
          createdAfter
        )
      );
    }
    long totalElements = entityManager
      .createQuery(countQuery)
      .getSingleResult();

    // Пагинация
    TypedQuery<HumanBeing> typedQuery = entityManager.createQuery(query);
    typedQuery.setFirstResult(page * size);
    typedQuery.setMaxResults(size);

    List<HumanBeing> content = typedQuery.getResultList();

    // Формируем ответ в формате Page
    Map<String, Object> result = new HashMap<>();
    result.put("content", content);
    result.put("totalElements", totalElements);
    result.put("totalPages", (totalElements + size - 1) / size);
    result.put("size", size);
    result.put("number", page);
    result.put("first", page == 0);
    result.put("last", page >= ((totalElements + size - 1) / size) - 1);

    return result;
  }

  private Predicate[] createPredicates(
    CriteriaBuilder cb,
    Root<HumanBeing> root,
    Long id,
    String name,
    Boolean hasToothpick,
    Boolean realHero,
    Integer impactSpeed,
    WeaponType weaponType,
    Mood mood,
    Integer teamId,
    LocalDateTime createdAfter
  ) {
    List<Predicate> predicates = new ArrayList<>();
    if (id != null) predicates.add(cb.equal(root.get("id"), id));
    if (name != null && !name.isEmpty()) {
      predicates.add(
        cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%")
      );
    }
    if (hasToothpick != null) predicates.add(
      cb.equal(root.get("hasToothpick"), hasToothpick)
    );
    if (realHero != null) predicates.add(
      cb.equal(root.get("realHero"), realHero)
    );
    if (impactSpeed != null) predicates.add(
      cb.equal(root.get("impactSpeed"), impactSpeed)
    );
    if (weaponType != null) predicates.add(
      cb.equal(root.get("weaponType"), weaponType)
    );
    if (mood != null) predicates.add(cb.equal(root.get("mood"), mood));
    if (teamId != null) predicates.add(cb.equal(root.get("teamId"), teamId));
    if (createdAfter != null) predicates.add(
      cb.greaterThan(root.get("creationDate"), createdAfter)
    );

    return predicates.toArray(new Predicate[0]);
  }

  @Override
  public HumanBeing getHumanBeingById(Long id) {
    HumanBeing humanBeing = entityManager.find(HumanBeing.class, id);
    if (humanBeing == null) {
      throw new RuntimeException("HumanBeing with id " + id + " not found");
    }
    return humanBeing;
  }

  @Override
  public HumanBeing createHumanBeing(HumanBeing humanBeing) {
    validateHumanBeing(humanBeing);
    entityManager.persist(humanBeing);
    entityManager.flush();
    return humanBeing;
  }

  @Override
  public HumanBeing updateHumanBeing(Long id, HumanBeing humanBeing) {
    HumanBeing existing = getHumanBeingById(id);
    validateHumanBeing(humanBeing);

    existing.setName(humanBeing.getName());
    existing.setCoordinates(humanBeing.getCoordinates());
    existing.setRealHero(humanBeing.getRealHero());
    existing.setHasToothpick(humanBeing.getHasToothpick());
    existing.setImpactSpeed(humanBeing.getImpactSpeed());
    existing.setWeaponType(humanBeing.getWeaponType());
    existing.setMood(humanBeing.getMood());
    existing.setCar(humanBeing.getCar());
    existing.setTeamId(humanBeing.getTeamId());

    entityManager.merge(existing);
    entityManager.flush();
    return existing;
  }

  @Override
  public void deleteHumanBeing(Long id) {
    HumanBeing humanBeing = getHumanBeingById(id);
    entityManager.remove(humanBeing);
  }

  @Override
  public long countByMood(Mood mood) {
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> query = cb.createQuery(Long.class);
    Root<HumanBeing> root = query.from(HumanBeing.class);
    query.select(cb.count(root));
    query.where(cb.equal(root.get("mood"), mood));
    return entityManager.createQuery(query).getSingleResult();
  }

  @Override
  public List<Integer> getUniqueImpactSpeeds() {
    TypedQuery<Integer> query = entityManager.createQuery(
      "SELECT DISTINCT h.impactSpeed FROM HumanBeing h WHERE h.impactSpeed IS NOT NULL ORDER BY h.impactSpeed",
      Integer.class
    );
    return query.getResultList();
  }

  @Override
  public long countByNameStartsWith(String prefix) {
    if (prefix == null || prefix.isEmpty()) {
      throw new IllegalArgumentException("Prefix cannot be null or empty");
    }
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> query = cb.createQuery(Long.class);
    Root<HumanBeing> root = query.from(HumanBeing.class);
    query.select(cb.count(root));
    query.where(
      cb.like(cb.lower(root.get("name")), prefix.toLowerCase() + "%")
    );
    return entityManager.createQuery(query).getSingleResult();
  }

  private void validateHumanBeing(HumanBeing hb) {
    if (hb.getName() == null || hb.getName().isEmpty()) {
      throw new IllegalArgumentException("Name cannot be null or empty");
    }
    if (hb.getCoordinates() == null) {
      throw new IllegalArgumentException("Coordinates cannot be null");
    }
    if (hb.getCoordinates().getX() == null) {
      throw new IllegalArgumentException("Coordinate X cannot be null");
    }
    if (
      hb.getCoordinates().getY() == null || hb.getCoordinates().getY() > 507
    ) {
      throw new IllegalArgumentException(
        "Coordinate Y cannot be null and must be <= 507"
      );
    }
    if (hb.getImpactSpeed() != null && hb.getImpactSpeed() < -441) {
      throw new IllegalArgumentException("Impact speed must be >= -441");
    }
    if (hb.getCar() != null && hb.getCar().getCool() == null) {
      throw new IllegalArgumentException("Car 'cool' property cannot be null");
    }
  }
}
