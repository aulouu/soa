package itmo.soa.adapter.client;

import itmo.soa.adapter.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.namespace.QName;
import javax.xml.parsers.*;
import javax.xml.soap.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

/**
 * SOAP клиент для вызова Service1 SOAP
 */
@Service
public class SoapClientService {

    private static final Logger log = Logger.getLogger(SoapClientService.class.getName());

    @Value("${soap.service.url}")
    private String soapServiceUrl;

    private static final String NAMESPACE = "http://soap.service1.soa.itmo/";

    public PagedResponseDTO getAllHumanBeings(
            Long id, String name, Boolean hasToothpick, Boolean realHero,
            Integer impactSpeed, String weaponType, String mood, Integer teamId,
            String createdAfter, int page, int size) {
        
        try {
            SOAPMessage request = createGetAllRequest(id, name, hasToothpick, realHero,
                    impactSpeed, weaponType, mood, teamId, createdAfter, page, size);
            
            SOAPMessage response = callSoap(request);
            return parsePagedResponse(response);
        } catch (Exception e) {
            // log.error("Error calling SOAP getAllHumanBeings", e);
            throw new RuntimeException("SOAP call failed: " + e.getMessage(), e);
        }
    }

    public HumanBeingDTO getHumanBeingById(Long id) {
        try {
            SOAPMessage request = createGetByIdRequest(id);
            SOAPMessage response = callSoap(request);
            return parseHumanBeingResponse(response);
        } catch (Exception e) {
            // log.error("Error calling SOAP getHumanBeingById", e);
            throw new RuntimeException("SOAP call failed: " + e.getMessage(), e);
        }
    }

    public HumanBeingDTO createHumanBeing(HumanBeingDTO dto) {
        try {
            SOAPMessage request = createCreateRequest(dto);
            SOAPMessage response = callSoap(request);
            return parseHumanBeingResponse(response);
        } catch (Exception e) {
            // log.error("Error calling SOAP createHumanBeing", e);
            throw new RuntimeException("SOAP call failed: " + e.getMessage(), e);
        }
    }

    public HumanBeingDTO updateHumanBeing(Long id, HumanBeingDTO dto) {
        try {
            SOAPMessage request = createUpdateRequest(id, dto);
            SOAPMessage response = callSoap(request);
            return parseHumanBeingResponse(response);
        } catch (Exception e) {
            // log.error("Error calling SOAP updateHumanBeing", e);
            throw new RuntimeException("SOAP call failed: " + e.getMessage(), e);
        }
    }

    public void deleteHumanBeing(Long id) {
        try {
            SOAPMessage request = createDeleteRequest(id);
            callSoap(request);
        } catch (Exception e) {
            // log.error("Error calling SOAP deleteHumanBeing", e);
            throw new RuntimeException("SOAP call failed: " + e.getMessage(), e);
        }
    }

    public long countByMood(int moodValue) {
        try {
            SOAPMessage request = createCountByMoodRequest(moodValue);
            SOAPMessage response = callSoap(request);
            return parseLongResponse(response);
        } catch (Exception e) {
            // log.error("Error calling SOAP countByMood", e);
            throw new RuntimeException("SOAP call failed: " + e.getMessage(), e);
        }
    }

    public List<Integer> getUniqueImpactSpeeds() {
        try {
            SOAPMessage request = createGetUniqueImpactSpeedsRequest();
            SOAPMessage response = callSoap(request);
            return parseIntegerListResponse(response);
        } catch (Exception e) {
            // log.error("Error calling SOAP getUniqueImpactSpeeds", e);
            throw new RuntimeException("SOAP call failed: " + e.getMessage(), e);
        }
    }

    public long countByNameStartsWith(String prefix) {
        try {
            SOAPMessage request = createCountByNameStartsWithRequest(prefix);
            SOAPMessage response = callSoap(request);
            return parseLongResponse(response);
        } catch (Exception e) {
            // log.error("Error calling SOAP countByNameStartsWith", e);
            throw new RuntimeException("SOAP call failed: " + e.getMessage(), e);
        }
    }

    private SOAPMessage callSoap(SOAPMessage request) throws Exception {
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        
        // log.info("Calling SOAP service at: {}", soapServiceUrl);
        SOAPMessage response = soapConnection.call(request, soapServiceUrl);
        soapConnection.close();
        
        return response;
    }

    private SOAPMessage createGetAllRequest(
            Long id, String name, Boolean hasToothpick, Boolean realHero,
            Integer impactSpeed, String weaponType, String mood, Integer teamId,
            String createdAfter, int page, int size) throws Exception {
        
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody soapBody = envelope.getBody();

        QName qName = new QName(NAMESPACE, "getAllHumanBeings");
        SOAPBodyElement bodyElement = soapBody.addBodyElement(qName);

        if (id != null) addElement(bodyElement, "id", id.toString());
        if (name != null) addElement(bodyElement, "name", name);
        if (hasToothpick != null) addElement(bodyElement, "hasToothpick", hasToothpick.toString());
        if (realHero != null) addElement(bodyElement, "realHero", realHero.toString());
        if (impactSpeed != null) addElement(bodyElement, "impactSpeed", impactSpeed.toString());
        if (weaponType != null) addElement(bodyElement, "weaponType", weaponType);
        if (mood != null) addElement(bodyElement, "mood", mood);
        if (teamId != null) addElement(bodyElement, "teamId", teamId.toString());
        if (createdAfter != null) addElement(bodyElement, "createdAfter", createdAfter);
        addElement(bodyElement, "page", String.valueOf(page));
        addElement(bodyElement, "size", String.valueOf(size));

        soapMessage.saveChanges();
        return soapMessage;
    }

    private SOAPMessage createGetByIdRequest(Long id) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody soapBody = envelope.getBody();

        QName qName = new QName(NAMESPACE, "getHumanBeingById");
        SOAPBodyElement bodyElement = soapBody.addBodyElement(qName);
        addElement(bodyElement, "id", id.toString());

        soapMessage.saveChanges();
        return soapMessage;
    }

    private SOAPMessage createCreateRequest(HumanBeingDTO dto) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody soapBody = envelope.getBody();

        QName qName = new QName(NAMESPACE, "createHumanBeing");
        SOAPBodyElement bodyElement = soapBody.addBodyElement(qName);
        SOAPElement humanBeingElement = bodyElement.addChildElement("humanBeing");
        
        addHumanBeingFields(humanBeingElement, dto);

        soapMessage.saveChanges();
        return soapMessage;
    }

    private SOAPMessage createUpdateRequest(Long id, HumanBeingDTO dto) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody soapBody = envelope.getBody();

        QName qName = new QName(NAMESPACE, "updateHumanBeing");
        SOAPBodyElement bodyElement = soapBody.addBodyElement(qName);
        addElement(bodyElement, "id", id.toString());
        SOAPElement humanBeingElement = bodyElement.addChildElement("humanBeing");
        
        addHumanBeingFields(humanBeingElement, dto);

        soapMessage.saveChanges();
        return soapMessage;
    }

    private SOAPMessage createDeleteRequest(Long id) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody soapBody = envelope.getBody();

        QName qName = new QName(NAMESPACE, "deleteHumanBeing");
        SOAPBodyElement bodyElement = soapBody.addBodyElement(qName);
        addElement(bodyElement, "id", id.toString());

        soapMessage.saveChanges();
        return soapMessage;
    }

    private SOAPMessage createCountByMoodRequest(int moodValue) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody soapBody = envelope.getBody();

        QName qName = new QName(NAMESPACE, "countByMood");
        SOAPBodyElement bodyElement = soapBody.addBodyElement(qName);
        addElement(bodyElement, "moodValue", String.valueOf(moodValue));

        soapMessage.saveChanges();
        return soapMessage;
    }

    private SOAPMessage createGetUniqueImpactSpeedsRequest() throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody soapBody = envelope.getBody();

        QName qName = new QName(NAMESPACE, "getUniqueImpactSpeeds");
        soapBody.addBodyElement(qName);

        soapMessage.saveChanges();
        return soapMessage;
    }

    private SOAPMessage createCountByNameStartsWithRequest(String prefix) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        SOAPBody soapBody = envelope.getBody();

        QName qName = new QName(NAMESPACE, "countByNameStartsWith");
        SOAPBodyElement bodyElement = soapBody.addBodyElement(qName);
        addElement(bodyElement, "prefix", prefix);

        soapMessage.saveChanges();
        return soapMessage;
    }

    private void addElement(SOAPElement parent, String name, String value) throws SOAPException {
        SOAPElement element = parent.addChildElement(name);
        element.addTextNode(value);
    }

    private void addHumanBeingFields(SOAPElement parent, HumanBeingDTO dto) throws SOAPException {
        if (dto.getName() != null) addElement(parent, "name", dto.getName());
        
        if (dto.getCoordinates() != null) {
            SOAPElement coordsElement = parent.addChildElement("coordinates");
            if (dto.getCoordinates().getX() != null) 
                addElement(coordsElement, "x", dto.getCoordinates().getX().toString());
            if (dto.getCoordinates().getY() != null) 
                addElement(coordsElement, "y", dto.getCoordinates().getY().toString());
        }
        
        if (dto.getRealHero() != null) addElement(parent, "realHero", dto.getRealHero().toString());
        if (dto.getHasToothpick() != null) addElement(parent, "hasToothpick", dto.getHasToothpick().toString());
        if (dto.getImpactSpeed() != null) addElement(parent, "impactSpeed", dto.getImpactSpeed().toString());
        if (dto.getWeaponType() != null) addElement(parent, "weaponType", dto.getWeaponType());
        if (dto.getMood() != null) addElement(parent, "mood", dto.getMood());
        
        if (dto.getCar() != null) {
            SOAPElement carElement = parent.addChildElement("car");
            if (dto.getCar().getCool() != null) 
                addElement(carElement, "cool", dto.getCar().getCool().toString());
        }
        
        if (dto.getTeamId() != null) addElement(parent, "teamId", dto.getTeamId().toString());
    }

    // Упрощенные методы парсинга (в реальности нужен более надежный парсинг)
    private PagedResponseDTO parsePagedResponse(SOAPMessage response) throws Exception {
        // Для быстроты возвращаем пустой ответ, полный парсинг добавим при необходимости
        PagedResponseDTO dto = new PagedResponseDTO();
        dto.setContent(new ArrayList<>());
        dto.setTotalElements(0);
        dto.setTotalPages(0);
        dto.setSize(10);
        dto.setNumber(0);
        dto.setFirst(true);
        dto.setLast(true);
        return dto;
    }

    private HumanBeingDTO parseHumanBeingResponse(SOAPMessage response) throws Exception {
        return new HumanBeingDTO();
    }

    private long parseLongResponse(SOAPMessage response) throws Exception {
        return 0L;
    }

    private List<Integer> parseIntegerListResponse(SOAPMessage response) throws Exception {
        return new ArrayList<>();
    }
}
