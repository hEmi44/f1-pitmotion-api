package pitmotion.env.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.Team;

public interface TeamRepository extends JpaRepository<Team, Long> { }
