package itmo.soa.adapter.rest;

import itmo.soa.adapter.soap.generated.*;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;

public class HumanBeingSoapClient {

    private HumanBeingService humanBeingService;

    public HumanBeingSoapClient() {
        try {
            URL url = new URL("http://localhost:8082/service1-soap/HumanBeingService?wsdl");
            QName qname = new QName("http://soap.service1.soa.itmo/", "HumanBeingService");
            Service service = Service.create(url, qname);
            humanBeingService = service.getPort(HumanBeingService.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SOAP client", e);
        }
    }

    public HumanBeingService getService() {
        return humanBeingService;
    }
}
