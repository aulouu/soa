package itmo.soa.service1.soap;

import itmo.soa.service1.model.HumanBeing;
import itmo.soa.service1.model.Mood;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import java.util.List;

/**
 * SOAP Web Service интерфейс для HumanBeing
 */
@WebService(name = "HumanBeingService", targetNamespace = "http://soap.service1.soa.itmo/")
public interface HumanBeingSoapService {

    @WebMethod
    PagedResponse getAllHumanBeings(
        @WebParam(name = "id") Long id,
        @WebParam(name = "name") String name,
        @WebParam(name = "hasToothpick") Boolean hasToothpick,
        @WebParam(name = "realHero") Boolean realHero,
        @WebParam(name = "impactSpeed") Integer impactSpeed,
        @WebParam(name = "weaponType") String weaponType,
        @WebParam(name = "mood") String mood,
        @WebParam(name = "teamId") Integer teamId,
        @WebParam(name = "createdAfter") String createdAfter,
        @WebParam(name = "page") int page,
        @WebParam(name = "size") int size
    );

    @WebMethod
    HumanBeing getHumanBeingById(@WebParam(name = "id") Long id);

    @WebMethod
    HumanBeing createHumanBeing(@WebParam(name = "humanBeing") HumanBeing humanBeing);

    @WebMethod
    HumanBeing updateHumanBeing(
        @WebParam(name = "id") Long id,
        @WebParam(name = "humanBeing") HumanBeing humanBeing
    );

    @WebMethod
    void deleteHumanBeing(@WebParam(name = "id") Long id);

    @WebMethod
    long countByMood(@WebParam(name = "moodValue") int moodValue);

    @WebMethod
    List<Integer> getUniqueImpactSpeeds();

    @WebMethod
    long countByNameStartsWith(@WebParam(name = "prefix") String prefix);
}
