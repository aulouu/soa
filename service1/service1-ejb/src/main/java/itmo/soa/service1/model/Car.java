package itmo.soa.service1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class Car implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "car_cool", nullable = true)
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
