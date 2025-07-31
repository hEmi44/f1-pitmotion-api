package pitmotion.env.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team_seasons")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class TeamSeason {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "team_seasons_seq")
  @SequenceGenerator(
      name = "team_seasons_seq",
      sequenceName = "team_seasons_seq",
      allocationSize = 1
  )
  private Long id;

  private Integer points;
  private Integer standings;
  private Integer wins;

  @ManyToOne(optional = false)
  @JoinColumn(name = "team_id", nullable = false)
  @JsonManagedReference
  private Team team;

  @ManyToOne(optional = false)
  @JoinColumn(name = "championship_id", nullable = false)
  @JsonManagedReference
  private Championship championship;

  @OneToMany(mappedBy = "teamSeason", cascade = CascadeType.ALL)
  @JsonBackReference
  private List<DriverSeason> driverSeasons = new ArrayList<>();
}
