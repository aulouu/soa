package itmo.soa.adapter.model;

import java.time.LocalDateTime;

public class HumanBeingDTO {
    private Long id;
    private String name;
    private CoordinatesDTO coordinates;
    private LocalDateTime creationDate;
    private Boolean realHero;
    private Boolean hasToothpick;
    private Integer impactSpeed;
    private String weaponType;
    private String mood;
    private CarDTO car;
    private Integer teamId;

    public HumanBeingDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CoordinatesDTO getCoordinates() { return coordinates; }
    public void setCoordinates(CoordinatesDTO coordinates) { this.coordinates = coordinates; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public Boolean getRealHero() { return realHero; }
    public void setRealHero(Boolean realHero) { this.realHero = realHero; }

    public Boolean getHasToothpick() { return hasToothpick; }
    public void setHasToothpick(Boolean hasToothpick) { this.hasToothpick = hasToothpick; }

    public Integer getImpactSpeed() { return impactSpeed; }
    public void setImpactSpeed(Integer impactSpeed) { this.impactSpeed = impactSpeed; }

    public String getWeaponType() { return weaponType; }
    public void setWeaponType(String weaponType) { this.weaponType = weaponType; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public CarDTO getCar() { return car; }
    public void setCar(CarDTO car) { this.car = car; }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
}
