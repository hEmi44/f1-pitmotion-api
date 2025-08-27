package pitmotion.env.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.User;


public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);
  Optional<User> findByEmail(String email);
  Optional<User> findById(Long id);
}
