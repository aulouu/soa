package itmo.soa.heroes.dto;

import itmo.soa.heroes.model.*;
import lombok.Data;

@Data
public class HumanBeingRequest {
    private String name;
    private Coordinates coordinates;
    private Boolean realHero;
    private Boolean hasToothpick;
    private Integer impactSpeed;
    private WeaponType weaponType;
    private Mood mood;
    private Car car;
    private Integer teamId;
}
