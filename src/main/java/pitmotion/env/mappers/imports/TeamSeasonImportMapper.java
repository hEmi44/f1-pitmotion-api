package pitmotion.env.mappers.imports;

import org.springframework.stereotype.Component;
import pitmotion.env.entities.TeamSeason;
import pitmotion.env.http.requests.imports.TeamSeasonImportRequest;

@Component
public class TeamSeasonImportMapper {

    public void request(TeamSeasonImportRequest request, TeamSeason entity) {
        entity.setPoints(request.points() != null ? request.points().intValue() : 0);
        entity.setStandings(request.position() != null ? request.position() : 0);
        entity.setWins(request.wins() != null ? request.wins() : 0);
    }
}
