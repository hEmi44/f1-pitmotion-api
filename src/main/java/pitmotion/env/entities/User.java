package pitmotion.env.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import pitmotion.env.enums.UserRole;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
  @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
  private Long id;

  private String username;
  private String email;
  private String password;

  @Column(name = "created_at")
  private LocalTime createdAt;

  @Enumerated(EnumType.STRING)
  private UserRole role;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference("user-trackers")
  private List<UserGpTracker> gpTrackers = new ArrayList<>();

  @PrePersist
  public void prePersist() {
    if (createdAt == null) createdAt = LocalTime.now();
  }

  public void addTracker(GrandPrix gp, Integer offsetMinutes) {
    UserGpTracker t = UserGpTracker.of(this, gp, offsetMinutes);
    gpTrackers.add(t);
  }

  public void removeTrackerByGpId(Long gpId) {
    for (Iterator<UserGpTracker> it = gpTrackers.iterator(); it.hasNext();) {
      UserGpTracker t = it.next();
      if (t.getGrandPrix() != null && gpId.equals(t.getGrandPrix().getId())) {
        it.remove();
        t.setUser(null);
        t.setGrandPrix(null);
      }
    }
  }
}
