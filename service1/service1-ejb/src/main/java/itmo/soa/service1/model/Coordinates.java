package itmo.soa.service1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;

@Embeddable
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Coordinates", namespace = "http://soap.service1.soa.itmo/")
public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "coordinate_x", nullable = false)
    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private Long x;

    @NotNull
    @Max(507)
    @Column(name = "coordinate_y", nullable = false)
    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private Integer y;

    public Coordinates() {}

    public Coordinates(Long x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Long getX() { return x; }
    public void setX(Long x) { this.x = x; }

    public Integer getY() { return y; }
    public void setY(Integer y) { this.y = y; }

    @Override
    public String toString() {
        return "Coordinates{x=" + x + ", y=" + y + '}';
    }
}
