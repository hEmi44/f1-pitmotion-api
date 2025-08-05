package pitmotion.env.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "circuits")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class Circuit  {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "circuits_seq")
  @SequenceGenerator(
      name = "circuits_seq",
      sequenceName = "circuits_seq",
      allocationSize = 1
  )
  private Long id;

  private String name;
  private String city;
  private Integer length;
  @Column(name = "lap_record", columnDefinition = "interval")
  private Duration lapRecord;
  @Column(name = "first_participation")
  private Integer firstParticipation;
  private Integer corners;
  @Column(name = "circuit_code")
  private String circuitCode;
  
  @ManyToOne
  @JoinColumn(name = "fastest_lap_by")
  @JsonManagedReference
  private DriverSeason fastestLapBy;

  @ManyToOne
  @JoinColumn(name = "country_id")
  @JsonManagedReference
  private Country country;

  private String url;

  @OneToMany(mappedBy = "circuit", cascade = CascadeType.ALL)
  @JsonBackReference
  private List<GrandPrix> grandPrix = new ArrayList<>();

}
