package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.entities.Championship;
import pitmotion.env.entities.Team;
import pitmotion.env.entities.TeamSeason;
import pitmotion.env.http.requests.imports.TeamSeasonImportRequest;
import pitmotion.env.http.requests.wrappers.TeamSeasonsImportWrapper;
import pitmotion.env.mappers.imports.TeamSeasonImportMapper;
import pitmotion.env.repositories.TeamRepository;
import pitmotion.env.repositories.TeamSeasonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("import")
public class TeamSeasonImportService implements EntityImportService<TeamSeasonImportRequest, TeamSeasonsImportWrapper> {

    private final RestClient restClient;
    private final TeamRepository teamRepository;
    private final TeamSeasonRepository teamSeasonRepository;
    private final TeamSeasonImportMapper mapper;

    public List<TeamSeason> importForChampionship(Championship championship) {
        List<TeamSeason> result = new ArrayList<>();
        String year = String.valueOf(championship.getYear());
    
        paginatedImport(
            offset -> restClient.get()
                .uri("/" + year + "/constructors-championship")
                .retrieve()
                .toEntity(TeamSeasonsImportWrapper.class)
                .getBody(),
            wrapper -> {
                if (wrapper == null || wrapper.teams() == null) return List.of();
                if (!championship.getChampionshipCode().equals(wrapper.championshipId())) return List.of();
                return wrapper.teams();
            },
            req -> {
                Optional<Team> teamOpt = teamRepository.findByTeamCode(req.teamId());
                if (teamOpt.isEmpty()) return;
    
                Team team = teamOpt.get();
                if (teamSeasonRepository.existsByTeamAndChampionship(team, championship)) return;
    
                TeamSeason entity = new TeamSeason();
                entity.setTeam(team);
                entity.setChampionship(championship);
                mapper.request(req, entity);
    
                result.add(teamSeasonRepository.save(entity));
            },
            100
        );
    
        return result;
    }
    
}
