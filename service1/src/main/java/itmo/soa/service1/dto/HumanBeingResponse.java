package itmo.soa.service1.dto;

import itmo.soa.service1.model.Car;
import itmo.soa.service1.model.Coordinates;
import itmo.soa.service1.model.Mood;
import itmo.soa.service1.model.WeaponType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HumanBeingResponse {
    private Long id;
    private String name;
    private Coordinates coordinates;
    private LocalDateTime creationDate;
    private Boolean realHero;
    private Boolean hasToothpick;
    private Integer impactSpeed;
    private WeaponType weaponType;
    private Mood mood;
    private Car car;
}
