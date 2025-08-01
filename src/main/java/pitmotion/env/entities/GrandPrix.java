package pitmotion.env.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grand_prix")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class GrandPrix {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grand_prix_seq")
  @SequenceGenerator(
      name = "grand_prix_seq",
      sequenceName = "grand_prix_seq",
      allocationSize = 1
  )
  private Long id;

  private String name;
  private Integer round;
  @Column(name = "grand_prix_code")
  private String grandPrixCode;
  private String url;
  @Column(name = "starting_date")
  private LocalDate startingDate;
  @Column(name = "ending_date")
  private LocalDate endingDate;

  @Column(name = "starting_time")
  private LocalTime startingTime;

  @Column(name = "ending_time")
  private LocalTime endingTime;


  @ManyToOne
  @JoinColumn(name = "championship_id")
  @JsonManagedReference
  private Championship championship;

  @ManyToOne
  @JoinColumn(name = "circuit_id")
  @JsonManagedReference
  private Circuit circuit;

  @OneToMany(mappedBy = "grandPrix", cascade = CascadeType.ALL)
  @JsonBackReference
  private List<GpSession> sessions = new ArrayList<>();

  @ManyToMany(mappedBy = "trackedGrandPrix")
  @JsonBackReference
  private List<User> usersTracking = new ArrayList<>();
}
