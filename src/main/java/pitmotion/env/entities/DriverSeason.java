package pitmotion.env.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "driver_seasons")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class DriverSeason {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "driver_seasons_seq")
  @SequenceGenerator(
      name = "driver_seasons_seq",
      sequenceName = "driver_seasons_seq",
      allocationSize = 1
  )
  private Long id;

  private Integer points;
  private Integer standings;
  private Integer wins;
  @Column(name = "driver_number")
  private String driverNumber;

  @ManyToOne(optional = false)
  @JoinColumn(name = "driver_id", nullable = false)
  @JsonManagedReference
  private Driver driver;

  @ManyToOne(optional = false)
  @JoinColumn(name = "team_season_id", nullable = false)
  @JsonManagedReference
  private TeamSeason teamSeason;

  @OneToMany(mappedBy = "driverSeason", cascade = CascadeType.ALL)
  @JsonBackReference
  private List<SessionResult> sessionResults = new java.util.ArrayList<>();
}
