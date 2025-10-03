package itmo.soa.service1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Car {

    @Column(name = "car_cool", nullable = true)
    private Boolean cool;
}
