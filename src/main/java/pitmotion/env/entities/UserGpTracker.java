package pitmotion.env.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_gp_tracker")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserGpTracker {

  @EmbeddedId
  private UserGpTrackerId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("userId")
  @JoinColumn(name = "user_id", nullable = false)
  @JsonBackReference("user-trackers")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("gpId")
  @JoinColumn(name = "gp_id", nullable = false)
  @JsonBackReference("gp-trackers")
  private GrandPrix grandPrix;

  @Column(name = "notification_offset_minutes", nullable = false)
  private Integer notificationOffsetMinutes = 10;

  public static UserGpTracker of(User user, GrandPrix gp, Integer offsetMinutes) {
    UserGpTracker t = new UserGpTracker();
    t.setId(new UserGpTrackerId(user.getId(), gp.getId()));
    t.setUser(user);
    t.setGrandPrix(gp);
    if (offsetMinutes != null) t.setNotificationOffsetMinutes(offsetMinutes);
    return t;
  }
}
