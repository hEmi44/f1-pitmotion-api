package pitmotion.env.mappers.imports;

import org.springframework.stereotype.Component;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.http.requests.imports.DriverSeasonImportRequest;

@Component
public class DriverSeasonImportMapper {

    public void request(DriverSeasonImportRequest request, DriverSeason entity) {
        entity.setPoints(request.points() != null ? request.points().intValue() : 0);
        entity.setStandings(request.position() != null ? request.position() : 0);
        entity.setWins(request.wins() != null ? request.wins() : 0);
        entity.setDriverNumber(
            request.driver() != null ? request.driver().number() : null
        );
    }
}
