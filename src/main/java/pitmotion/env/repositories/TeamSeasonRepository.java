package pitmotion.env.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pitmotion.env.entities.Championship;
import pitmotion.env.entities.Team;
import pitmotion.env.entities.TeamSeason;

import java.util.Optional;

@Repository
public interface TeamSeasonRepository extends JpaRepository<TeamSeason, Long> {
    boolean existsByTeamAndChampionship(Team team, Championship championship);
    Optional<TeamSeason> findByTeamAndChampionship(Team team, Championship championship);
}
