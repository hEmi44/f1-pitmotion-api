package pitmotion.env.repositories.customs;

import pitmotion.env.entities.Driver;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.Team;

import java.util.Optional;

public interface DriverSeasonRepositoryCustom {
    Optional<DriverSeason> findByDriverTeamAndYear(Driver driver, Team team, int year);
    
}
