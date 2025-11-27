package itmo.soa.service1.soap;

import itmo.soa.service1.ejb.HumanBeingServiceLocal;
import itmo.soa.service1.model.HumanBeing;
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
        
        logger.info("SOAP getAllHumanBeings called");

        try {
            WeaponType weaponTypeEnum = weaponType != null ? WeaponType.valueOf(weaponType.toUpperCase()) : null;
            Mood moodEnum = mood != null ? Mood.valueOf(mood.toUpperCase()) : null;
            LocalDateTime createdAfterDate = null;
            if (createdAfter != null && !createdAfter.isEmpty()) {
                createdAfterDate = LocalDateTime.parse(createdAfter, DateTimeFormatter.ISO_DATE_TIME);
            }

            Map<String, Object> result = humanBeingService.getAllHumanBeings(
                id, name, hasToothpick, realHero, impactSpeed, 
                weaponTypeEnum, moodEnum, teamId, createdAfterDate, page, size
            );

            @SuppressWarnings("unchecked")
            List<HumanBeing> content = (List<HumanBeing>) result.get("content");
            long totalElements = ((Number) result.get("totalElements")).longValue();
            long totalPages = ((Number) result.get("totalPages")).longValue();
            boolean first = (Boolean) result.get("first");
            boolean last = (Boolean) result.get("last");

            return new PagedResponse(content, totalElements, totalPages, size, page, first, last);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in SOAP getAllHumanBeings", e);
            throw new RuntimeException("Error getting all human beings: " + e.getMessage());
        }
    }

    @Override
    public HumanBeing getHumanBeingById(Long id) {
        logger.info("SOAP getHumanBeingById called with id: " + id);
        try {
            return humanBeingService.getHumanBeingById(id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in SOAP getHumanBeingById", e);
            throw new RuntimeException("Error getting human being by id: " + e.getMessage());
        }
    }

    @Override
    public HumanBeing createHumanBeing(HumanBeing humanBeing) {
        logger.info("SOAP createHumanBeing called");
        try {
            return humanBeingService.createHumanBeing(humanBeing);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in SOAP createHumanBeing", e);
            throw new RuntimeException("Error creating human being: " + e.getMessage());
        }
    }

    @Override
    public HumanBeing updateHumanBeing(Long id, HumanBeing humanBeing) {
        logger.info("SOAP updateHumanBeing called with id: " + id);
        try {
            return humanBeingService.updateHumanBeing(id, humanBeing);
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
