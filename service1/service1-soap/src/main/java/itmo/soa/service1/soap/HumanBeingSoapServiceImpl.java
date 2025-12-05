package itmo.soa.service1.soap;

import itmo.soa.service1.ejb.HumanBeingServiceLocal;
import itmo.soa.service1.model.HumanBeing;
import itmo.soa.service1.soap.dto.HumanBeingDTO;
import itmo.soa.service1.model.Mood;
import itmo.soa.service1.model.WeaponType;
import jakarta.ejb.EJB;
import jakarta.jws.WebService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * SOAP Web Service реализация для HumanBeing
 * Делегирует вызовы к EJB бизнес-логике
 */
@WebService(
        serviceName = "HumanBeingService",
        portName = "HumanBeingPort",
        targetNamespace = "http://soap.service1.soa.itmo/",
        endpointInterface = "itmo.soa.service1.soap.HumanBeingSoapService"
)
public class HumanBeingSoapServiceImpl implements HumanBeingSoapService {

    private static final Logger logger = Logger.getLogger(HumanBeingSoapServiceImpl.class.getName());

    @EJB
    private HumanBeingServiceLocal humanBeingService;

    @Override
    public PagedResponse getAllHumanBeings(
            Long id, String name, Boolean hasToothpick, Boolean realHero,
            Integer impactSpeed, String weaponType, String mood, Integer teamId,
            String createdAfter, int page, int size) {

        logger.info("SOAP getAllHumanBeings called with parameters: " +
                "id=" + id + ", name=" + name + ", page=" + page + ", size=" + size);

        // Нормализуем номер страницы
        int normalizedPage = page;
        if (page < 1) {
            normalizedPage = 1;
            logger.warning("Page parameter normalized from " + page + " to " + normalizedPage);
        }

        // Валидация размера страницы
        if (size < 1) {
            size = 10; // default size
            logger.warning("Size parameter normalized to " + size);
        }
        if (size > 100) {
            size = 100; // max size
            logger.warning("Size parameter limited to " + size);
        }

        try {
            WeaponType weaponTypeEnum = weaponType != null ? WeaponType.valueOf(weaponType.toUpperCase()) : null;
            Mood moodEnum = mood != null ? Mood.valueOf(mood.toUpperCase()) : null;
            LocalDateTime createdAfterDate = null;
            if (createdAfter != null && !createdAfter.isEmpty()) {
                try {
                    createdAfterDate = LocalDateTime.parse(createdAfter, DateTimeFormatter.ISO_DATE_TIME);
                } catch (Exception e) {
                    logger.warning("Failed to parse createdAfter date: " + createdAfter);
                }
            }

            // Используем normalizedPage - 1 для EJB (если EJB ожидает 0-based)
            // ИЛИ используем normalizedPage (если EJB ожидает 1-based)
            Map<String, Object> result = humanBeingService.getAllHumanBeings(
                    id, name, hasToothpick, realHero, impactSpeed,
                    weaponTypeEnum, moodEnum, teamId, createdAfterDate,
                    normalizedPage - 1, size  // ← НОРМАЛИЗАЦИЯ здесь!
            );

            @SuppressWarnings("unchecked")
            List<HumanBeing> content = (List<HumanBeing>) result.get("content");

            // Конвертируем Entity в DTO
            List<HumanBeingDTO> dtoContent = content.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            long totalElements = ((Number) result.get("totalElements")).longValue();
            long totalPages = ((Number) result.get("totalPages")).longValue();
            boolean first = (Boolean) result.get("first");
            boolean last = (Boolean) result.get("last");

            logger.info("Returning PagedResponse with " + dtoContent.size() + " items, totalElements: " + totalElements);

            // Возвращаем с normalizedPage (1-based для клиента)
            return new PagedResponse(dtoContent, totalElements, totalPages, size, normalizedPage, first, last);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in SOAP getAllHumanBeings", e);
            throw new RuntimeException("Error getting all human beings: " + e.getMessage());
        }
    }

    @Override
    public HumanBeingDTO getHumanBeingById(Long id) {
        logger.info("SOAP getHumanBeingById called with id: " + id);
        try {
            HumanBeing entity = humanBeingService.getHumanBeingById(id);
            if (entity == null) {
                logger.warning("HumanBeing not found with id: " + id);
                return null;
            }
            return convertToDTO(entity);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in SOAP getHumanBeingById", e);
            throw new RuntimeException("Error getting human being by id: " + e.getMessage());
        }
    }

    @Override
    public HumanBeingDTO createHumanBeing(HumanBeingDTO humanBeingDTO) {
        logger.info("SOAP createHumanBeing called");
        try {
            // Конвертируем DTO в Entity
            HumanBeing entity = convertToEntity(humanBeingDTO);
            HumanBeing createdEntity = humanBeingService.createHumanBeing(entity);
            return convertToDTO(createdEntity);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in SOAP createHumanBeing", e);
            throw new RuntimeException("Error creating human being: " + e.getMessage());
        }
    }

    @Override
    public HumanBeingDTO updateHumanBeing(Long id, HumanBeingDTO humanBeingDTO) {
        logger.info("SOAP updateHumanBeing called with id: " + id);
        try {
            // Конвертируем DTO в Entity
            HumanBeing entity = convertToEntity(humanBeingDTO);
            entity.setId(id); // Устанавливаем ID из параметра
            HumanBeing updatedEntity = humanBeingService.updateHumanBeing(id, entity);
            return convertToDTO(updatedEntity);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in SOAP updateHumanBeing", e);
            throw new RuntimeException("Error updating human being: " + e.getMessage());
        }
    }

    @Override
    public void deleteHumanBeing(Long id) {
        logger.info("SOAP deleteHumanBeing called with id: " + id);
        try {
            humanBeingService.deleteHumanBeing(id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in SOAP deleteHumanBeing", e);
            throw new RuntimeException("Error deleting human being: " + e.getMessage());
        }
    }

    @Override
    public long countByMood(int moodValue) {
        logger.info("SOAP countByMood called with value: " + moodValue);
        try {
            Mood mood = convertMoodValue(moodValue);
            return humanBeingService.countByMood(mood);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in SOAP countByMood", e);
            throw new RuntimeException("Error counting by mood: " + e.getMessage());
        }
    }

    @Override
    public List<Integer> getUniqueImpactSpeeds() {
        logger.info("SOAP getUniqueImpactSpeeds called");
        try {
            return humanBeingService.getUniqueImpactSpeeds();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in SOAP getUniqueImpactSpeeds", e);
            throw new RuntimeException("Error getting unique impact speeds: " + e.getMessage());
        }
    }

    @Override
    public long countByNameStartsWith(String prefix) {
        logger.info("SOAP countByNameStartsWith called with prefix: " + prefix);
        try {
            return humanBeingService.countByNameStartsWith(prefix);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in SOAP countByNameStartsWith", e);
            throw new RuntimeException("Error counting by name prefix: " + e.getMessage());
        }
    }

    // Вспомогательные методы для конвертации

    private HumanBeingDTO convertToDTO(HumanBeing entity) {
        if (entity == null) {
            return null;
        }
        return new HumanBeingDTO(entity);
    }

    private HumanBeing convertToEntity(HumanBeingDTO dto) {
        if (dto == null) {
            return null;
        }

        HumanBeing entity = new HumanBeing();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setCoordinates(dto.getCoordinates());
        entity.setCreationDate(dto.getCreationDate());
        entity.setRealHero(dto.getRealHero());
        entity.setHasToothpick(dto.getHasToothpick());
        entity.setImpactSpeed(dto.getImpactSpeed());
        entity.setWeaponType(dto.getWeaponType());
        entity.setMood(dto.getMood());
        entity.setCar(dto.getCar());
        entity.setTeamId(dto.getTeamId());

        return entity;
    }

    private Mood convertMoodValue(int value) {
        switch (value) {
            case 0: return Mood.SADNESS;
            case 1: return Mood.SORROW;
            case 2: return Mood.APATHY;
            case 3: return Mood.FRENZY;
            default: throw new IllegalArgumentException("Invalid mood value: " + value);
        }
    }
}
