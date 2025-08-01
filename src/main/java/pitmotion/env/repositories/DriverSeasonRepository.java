package pitmotion.env.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.Driver;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.TeamSeason;
import pitmotion.env.repositories.customs.DriverSeasonRepositoryCustom;

public interface DriverSeasonRepository extends JpaRepository<DriverSeason, Long>, DriverSeasonRepositoryCustom {
    boolean existsByDriverAndTeamSeason(Driver driver, TeamSeason teamSeason);
}
