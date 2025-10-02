package itmo.soa.service1.dto;

import itmo.soa.service1.model.Car;
import itmo.soa.service1.model.Coordinates;
import itmo.soa.service1.model.Mood;
import itmo.soa.service1.model.WeaponType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
