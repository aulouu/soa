package itmo.soa.service1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "human_beings")
public class HumanBeing implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Embedded
    private Coordinates coordinates;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate;

    @Column(name = "real_hero")
    private Boolean realHero;

    @Column(name = "has_toothpick")
    private Boolean hasToothpick;

    @Min(-441)
    @Column(name = "impact_speed")
    private Integer impactSpeed;

    @Enumerated(EnumType.STRING)
    @Column(name = "weapon_type")
    private WeaponType weaponType;

    @Enumerated(EnumType.STRING)
    @Column(name = "mood")
    private Mood mood;

    @Embedded
    private Car car;

    @Column(name = "team_id", nullable = true)
    private Integer teamId;

    public HumanBeing() {}

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
        if (this.weaponType != null) {
            this.weaponType = WeaponType.valueOf(this.weaponType.name().toUpperCase());
        }
        if (this.mood != null) {
            this.mood = Mood.valueOf(this.mood.name().toUpperCase());
        }
        if (teamId == null) {
            teamId = (int) (Math.random() * 10) + 1;
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

    @Override
    public String toString() {
        return "HumanBeing{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", realHero=" + realHero +
                ", hasToothpick=" + hasToothpick +
                ", impactSpeed=" + impactSpeed +
                ", weaponType=" + weaponType +
                ", mood=" + mood +
                ", car=" + car +
                ", teamId=" + teamId +
                '}';
    }
}
