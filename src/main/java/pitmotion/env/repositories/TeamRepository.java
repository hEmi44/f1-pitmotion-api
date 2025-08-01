package pitmotion.env.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.Team;

public interface TeamRepository extends JpaRepository<Team, Long> { 
    boolean existsByTeamCode(String teamCode);
    Optional<Team> findByTeamCode(String teamCodeCode);
}
