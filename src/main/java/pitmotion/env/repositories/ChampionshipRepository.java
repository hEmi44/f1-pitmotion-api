package pitmotion.env.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.Championship;

public interface ChampionshipRepository extends JpaRepository<Championship, Long> { 
    boolean existsByChampionshipCode(String championshipCode);
    Optional<Championship> findByChampionshipCode(String championshipCode);
}
