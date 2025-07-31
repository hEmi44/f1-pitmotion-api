package pitmotion.env.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "drivers")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class Driver {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "drivers_seq")
  @SequenceGenerator(
      name = "drivers_seq",
      sequenceName = "drivers_seq",
      allocationSize = 1
  )
  private Long id;

  private String name;
  private String surname;
  private LocalDate birthday;
  @Column(name = "short_name")
  private String shortName;
  @Column(name = "driver_code")
  private String driverCode;
  private String url;

  @ManyToOne
  @JoinColumn(name = "country_id")
  @JsonManagedReference
  private Country country;

  @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
  @JsonBackReference
  private List<DriverSeason> driverSeasons = new ArrayList<>();
}
