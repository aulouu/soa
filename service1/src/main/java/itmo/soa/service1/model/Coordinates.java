package itmo.soa.service1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Coordinates {
    @NotNull
    @Column(name = "coordinate_x", nullable = false)
    private Long x;

    @NotNull
    @Max(507)
    @Column(name = "coordinate_y", nullable = false)
    private Integer y;
}
