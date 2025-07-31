package pitmotion.env.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "championships")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class Championship {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "championships_seq")
  @SequenceGenerator(
      name = "championships_seq",
      sequenceName = "championships_seq",
      allocationSize = 1
  )
  private Long id;

  private String name;
  private Integer year;
  @Column(name = "championship_code")
  private String championshipCode;
  private String url;

  @OneToMany(mappedBy = "championship", cascade = CascadeType.ALL)
  @JsonBackReference
  private List<TeamSeason> teamSeasons = new ArrayList<>();

  @OneToMany(mappedBy = "championship", cascade = CascadeType.ALL)
  @JsonBackReference
  private List<GrandPrix> grandPrix = new ArrayList<>();
}
