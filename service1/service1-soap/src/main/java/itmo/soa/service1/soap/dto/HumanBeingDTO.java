package itmo.soa.service1.soap.dto;

import itmo.soa.service1.model.Car;
import itmo.soa.service1.model.Coordinates;
import itmo.soa.service1.model.Mood;
import itmo.soa.service1.model.WeaponType;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.time.LocalDateTime;

@XmlRootElement(name = "HumanBeing", namespace = "http://soap.service1.soa.itmo/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace = "http://soap.service1.soa.itmo/", propOrder = {
        "id", "name", "coordinates", "creationDate", "realHero",
        "hasToothpick", "impactSpeed", "weaponType", "mood", "car", "teamId"
})
public class HumanBeingDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private Long id;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private String name;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private Coordinates coordinates;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private LocalDateTime creationDate;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private Boolean realHero;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private Boolean hasToothpick;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private Integer impactSpeed;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private WeaponType weaponType;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private Mood mood;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private Car car;

    @XmlElement(namespace = "http://soap.service1.soa.itmo/")
    private Integer teamId;

    // Конструкторы
    public HumanBeingDTO() {}

    public HumanBeingDTO(itmo.soa.service1.model.HumanBeing entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.name = entity.getName();
            this.coordinates = entity.getCoordinates();
            this.creationDate = entity.getCreationDate();
            this.realHero = entity.getRealHero();
            this.hasToothpick = entity.getHasToothpick();
            this.impactSpeed = entity.getImpactSpeed();
            this.weaponType = entity.getWeaponType();
            this.mood = entity.getMood();
            this.car = entity.getCar();
            this.teamId = entity.getTeamId();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Coordinates getCoordinates() { return coordinates; }
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public Boolean getRealHero() { return realHero; }
    public void setRealHero(Boolean realHero) { this.realHero = realHero; }

    public Boolean getHasToothpick() { return hasToothpick; }
    public void setHasToothpick(Boolean hasToothpick) { this.hasToothpick = hasToothpick; }

    public Integer getImpactSpeed() { return impactSpeed; }
    public void setImpactSpeed(Integer impactSpeed) { this.impactSpeed = impactSpeed; }

    public WeaponType getWeaponType() { return weaponType; }
    public void setWeaponType(WeaponType weaponType) { this.weaponType = weaponType; }

    public Mood getMood() { return mood; }
    public void setMood(Mood mood) { this.mood = mood; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    public Integer getTeamId() { return teamId; }
    public void setTeamId(Integer teamId) { this.teamId = teamId; }
}
