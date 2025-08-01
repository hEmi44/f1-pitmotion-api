package pitmotion.env.mappers.imports;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pitmotion.env.entities.Team;
import pitmotion.env.http.requests.imports.TeamImportRequest;

@Component
@RequiredArgsConstructor
public class TeamImportMapper {

    private final CountryResolver countryResolver;

    public Team request(TeamImportRequest req, Team target) {
        if (req.country() != null) {
            target.setCountry(countryResolver.resolve(req.country()));
        }
    
        target.setTeamCode(req.teamCode());
        target.setName(req.name());
        target.setFirstAppearance(req.firstAppearance());
        target.setConstructorsChampionships(req.constructorsChampionships() != null ? req.constructorsChampionships() : 0);
        target.setDriversChampionships(req.driversChampionships() != null ? req.driversChampionships() : 0);
        target.setUrl(req.url());
    
        return target;
    }
    
}
