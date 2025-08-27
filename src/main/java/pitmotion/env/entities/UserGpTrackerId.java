package pitmotion.env.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class UserGpTrackerId implements Serializable {
  private Long userId;
  private Long gpId;
}
