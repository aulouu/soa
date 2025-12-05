package itmo.soa.service1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;

@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Car", namespace = "http://soap.service1.soa.itmo/")
public class Car implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "car_cool", nullable = true)
    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private Boolean cool;

    public Car() {}

    public Car(Boolean cool) {
        this.cool = cool;
    }

    public Boolean getCool() { return cool; }
    public void setCool(Boolean cool) { this.cool = cool; }

    @Override
    public String toString() {
        return "Car{cool=" + cool + '}';
    }
}
