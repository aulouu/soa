package itmo.soa.service1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

@Embeddable
public class Coordinates implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Column(name = "coordinate_x", nullable = false)
    private Long x;

    @NotNull
    @Max(507)
    @Column(name = "coordinate_y", nullable = false)
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
