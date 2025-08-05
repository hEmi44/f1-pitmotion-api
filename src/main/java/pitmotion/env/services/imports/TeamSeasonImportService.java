package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.configurations.ImportProperties;
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
public class TeamSeasonImportService
    extends EntityImportService<TeamSeasonImportRequest, TeamSeasonsImportWrapper> {

    private final RestClient restClient;
    private final TeamRepository teamRepository;
    private final TeamSeasonRepository teamSeasonRepository;
    private final TeamSeasonImportMapper mapper;
    private final ImportProperties importProperties;

    public List<TeamSeason> importForChampionship(Championship championship) {
        List<TeamSeason> result = new ArrayList<>();
        String year = String.valueOf(championship.getYear());

        paginatedImport(
            offset -> fetch(() ->
                restClient.get()
                          .uri("/" + year + "/constructors-championship?limit="
                               + importProperties.getPageSize()
                               + "&offset=" + offset)
                          .retrieve()
                          .toEntity(TeamSeasonsImportWrapper.class)
                          .getBody()
            ),
            wrapper -> {
                if (wrapper == null || wrapper.teams() == null) {
                    return List.<TeamSeasonImportRequest>of();
                }
                if (!championship.getChampionshipCode().equals(wrapper.championshipId())) {
                    return List.<TeamSeasonImportRequest>of();
                }
                return wrapper.teams();
            },
            req -> processTeamSeason(req, championship, result),
            importProperties.getPageSize()
        );

        return result;
    }

    private void processTeamSeason(TeamSeasonImportRequest req,
                                   Championship championship,
                                   List<TeamSeason> result) {
        Optional<Team> teamOpt = teamRepository.findByTeamCode(req.teamId());
        if (teamOpt.isEmpty()) {
            return;
        }

        Team team = teamOpt.get();
        if (teamSeasonRepository.existsByTeamAndChampionship(team, championship)) {
            return;
        }

        TeamSeason entity = new TeamSeason();
        entity.setTeam(team);
        entity.setChampionship(championship);
        mapper.request(req, entity);

        result.add(teamSeasonRepository.save(entity));
    }
}
