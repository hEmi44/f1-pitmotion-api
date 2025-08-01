package pitmotion.env.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import pitmotion.env.enums.SessionType;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "gp_sessions")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class GpSession {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gp_sessions_seq")
  @SequenceGenerator(
      name = "gp_sessions_seq",
      sequenceName = "gp_sessions_seq",
      allocationSize = 1
  )
  private Long id;

  private Integer laps;
  private LocalDate date;
  @Column(name = "time")
  private LocalTime time;  

  @Column(name = "fast_lap")
  private String fastLap;

  @ManyToOne
  @JoinColumn(name = "fast_lap_by")
  @JsonManagedReference
  private Driver fastLapBy;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private SessionType type;



  @ManyToOne
  @JoinColumn(name = "grand_prix_id")
  @JsonManagedReference
  private GrandPrix grandPrix;

  @OneToMany(mappedBy = "gpSession", cascade = CascadeType.ALL)
  @JsonBackReference
  private List<SessionResult> results = new ArrayList<>();
}
