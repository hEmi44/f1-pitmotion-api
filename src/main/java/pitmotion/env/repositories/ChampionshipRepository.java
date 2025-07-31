package pitmotion.env.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.Championship;

public interface ChampionshipRepository extends JpaRepository<Championship, Long> { }
