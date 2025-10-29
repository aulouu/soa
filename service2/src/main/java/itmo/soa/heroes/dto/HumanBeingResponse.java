package itmo.soa.heroes.dto;

import itmo.soa.heroes.model.*;
import lombok.Data;

@Data
public class HumanBeingResponse {
    private Long id;
    private String name;
    private Coordinates coordinates;
    private String creationDate;
    private Boolean realHero;
    private Boolean hasToothpick;
    private Integer impactSpeed;
    private WeaponType weaponType;
    private Mood mood;
    private Car car;
}
