package pitmotion.env.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aliases")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @EqualsAndHashCode(of = "id")
public class Alias {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aliases_seq")
  @SequenceGenerator(name = "aliases_seq", sequenceName = "aliases_id_seq", allocationSize = 1)
  private Long id;

  @Column(name = "alias", nullable = false, length = 255)
  private String alias;

  @Enumerated(EnumType.STRING)
  @Column(name = "entity_type", nullable = false, length = 50)
  private EntityType entityType;

  @Column(name = "entity_id", nullable = false)
  private Long entityId;
}
