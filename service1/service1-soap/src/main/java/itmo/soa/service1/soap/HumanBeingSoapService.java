package itmo.soa.service1.soap;

import itmo.soa.service1.soap.dto.HumanBeingDTO;
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
            @WebParam(name = "id", targetNamespace = "http://soap.service1.soa.itmo/") Long id,
            @WebParam(name = "name", targetNamespace = "http://soap.service1.soa.itmo/") String name,
            @WebParam(name = "hasToothpick", targetNamespace = "http://soap.service1.soa.itmo/") Boolean hasToothpick,
            @WebParam(name = "realHero", targetNamespace = "http://soap.service1.soa.itmo/") Boolean realHero,
            @WebParam(name = "impactSpeed", targetNamespace = "http://soap.service1.soa.itmo/") Integer impactSpeed,
            @WebParam(name = "weaponType", targetNamespace = "http://soap.service1.soa.itmo/") String weaponType,
            @WebParam(name = "mood", targetNamespace = "http://soap.service1.soa.itmo/") String mood,
            @WebParam(name = "teamId", targetNamespace = "http://soap.service1.soa.itmo/") Integer teamId,
            @WebParam(name = "createdAfter", targetNamespace = "http://soap.service1.soa.itmo/") String createdAfter,
            @WebParam(name = "page", targetNamespace = "http://soap.service1.soa.itmo/") int page,
            @WebParam(name = "size", targetNamespace = "http://soap.service1.soa.itmo/") int size
    );

    @WebMethod
    HumanBeingDTO getHumanBeingById(@WebParam(name = "id", targetNamespace = "http://soap.service1.soa.itmo/") Long id);

    @WebMethod
    HumanBeingDTO createHumanBeing(@WebParam(name = "humanBeing", targetNamespace = "http://soap.service1.soa.itmo/") HumanBeingDTO humanBeing);

    @WebMethod
    HumanBeingDTO updateHumanBeing(
            @WebParam(name = "id", targetNamespace = "http://soap.service1.soa.itmo/") Long id,
            @WebParam(name = "humanBeing", targetNamespace = "http://soap.service1.soa.itmo/") HumanBeingDTO humanBeing
    );

    @WebMethod
    void deleteHumanBeing(@WebParam(name = "id", targetNamespace = "http://soap.service1.soa.itmo/") Long id);

    @WebMethod
    long countByMood(@WebParam(name = "moodValue", targetNamespace = "http://soap.service1.soa.itmo/") int moodValue);

    @WebMethod
    List<Integer> getUniqueImpactSpeeds();

    @WebMethod
    long countByNameStartsWith(@WebParam(name = "prefix", targetNamespace = "http://soap.service1.soa.itmo/") String prefix);
}
