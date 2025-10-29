package itmo.soa.service1.ejb;

import itmo.soa.service1.model.HumanBeing;
import itmo.soa.service1.model.Mood;
import itmo.soa.service1.model.WeaponType;
import jakarta.ejb.Local;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Remote интерфейс для EJB компонента с бизнес-логикой
 */
@Local
public interface HumanBeingServiceLocal {
  /**
   * Получить всех HumanBeing с фильтрацией и пагинацией
   */
  Map<String, Object> getAllHumanBeings(
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
  );

  /**
   * Получить HumanBeing по ID
   */
  HumanBeing getHumanBeingById(Long id);

  /**
   * Создать нового HumanBeing
   */
  HumanBeing createHumanBeing(HumanBeing humanBeing);

  /**
   * Обновить существующего HumanBeing
   */
  HumanBeing updateHumanBeing(Long id, HumanBeing humanBeing);

  /**
   * Удалить HumanBeing
   */
  void deleteHumanBeing(Long id);

  /**
   * Получить количество HumanBeing по настроению
   */
  long countByMood(Mood mood);

  /**
   * Получить уникальные значения impactSpeed
   */
  List<Integer> getUniqueImpactSpeeds();
}
