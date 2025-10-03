package itmo.soa.service1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "human_beings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class HumanBeing {

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

    @PrePersist
    protected void onCreate() {
        if (creationDate == null) {
            creationDate = LocalDateTime.now();
        }
        if (this.weaponType != null) {
            this.weaponType = WeaponType.valueOf(
                    this.weaponType.name().toUpperCase()
            );
        }
        if (this.mood != null) {
            this.mood = Mood.valueOf(this.mood.name().toUpperCase());
        }
        if (teamId == null) {
            teamId = (int) (Math.random() * 10) + 1;
        }
    }
}
