package pitmotion.env.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class Team {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teams_seq")
  @SequenceGenerator(
      name = "teams_seq",
      sequenceName = "teams_seq",
      allocationSize = 1
  )
  private Long id;

  private String name;
  @Column(name = "first_appearance")
  private Integer firstAppearance;
  @Column(name = "constructors_championships")
  private Integer constructorsChampionships;
  @Column(name = "drivers_championships")
  private Integer driversChampionships;
  @Column(name = "team_code")
  private String teamCode;
  private String url;

  @ManyToOne
  @JoinColumn(name = "country_id")
  @JsonManagedReference
  private Country country;

  @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
  @JsonBackReference
  private List<TeamSeason> teamSeasons = new ArrayList<>();
}
