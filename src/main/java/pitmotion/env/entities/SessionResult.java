package pitmotion.env.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "sessions_results")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class SessionResult {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sessions_results_seq")
  @SequenceGenerator(
      name = "sessions_results_seq",
      sequenceName = "sessions_results_seq",
      allocationSize = 1
  )
  private Long id;

  private String time;
  @Column(name = "lap_time")
  private String lapTime;
  @Column(name = "lap_time_seconds")
  private Integer lapTimeSeconds;
  private Integer position;
  @Column(name = "q1_time")
  private String q1Time;
  @Column(name = "q2_time")
  private String q2Time;
  @Column(name = "q3_time")
  private String q3Time;
  @Column(name = "grid_position")
  private Integer gridPosition;
  private Integer points;
  @Column(name = "grid_starting_position")
  private Integer gridStartingPosition;
  private String status;

  @ManyToOne
  @JoinColumn(name = "gp_session_id")
  @JsonManagedReference
  private GP_Session gpSession;

  @ManyToOne
  @JoinColumn(name = "driver_season_id")
  @JsonManagedReference
  private DriverSeason driverSeason;
}
