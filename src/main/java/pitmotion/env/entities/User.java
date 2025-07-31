package pitmotion.env.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
  @SequenceGenerator(
      name = "users_seq",
      sequenceName = "users_seq",
      allocationSize = 1
  )
  private Long id;

  private String username;
  private String email;
  private String password;
  @Column(name = "created_at")
  private LocalTime createdAt;
  private String role;

  @ManyToMany
  @JoinTable(
    name = "user_gp_tracker",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "gp_id")
  )
  @JsonManagedReference
  private List<GrandPrix> trackedGrandPrix = new ArrayList<>();
}
