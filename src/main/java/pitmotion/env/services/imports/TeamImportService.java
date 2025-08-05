package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import pitmotion.env.entities.Team;
import pitmotion.env.http.requests.imports.TeamImportRequest;
import pitmotion.env.http.requests.wrappers.TeamsImportWrapper;
import pitmotion.env.mappers.imports.TeamImportMapper;
import pitmotion.env.repositories.TeamRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class TeamImportService extends EntityImportService<TeamImportRequest, TeamsImportWrapper> {

    private final RestClient restClient;
    private final TeamRepository repo;
    private final TeamImportMapper mapper;

    public List<Team> importTeams() {
        List<Team> result = new ArrayList<>();

        paginatedImport(
            offset -> fetch(() ->
                restClient.get()
                          .uri("/teams?limit=" + importProperties.getPageSize() + "&offset=" + offset)
                          .retrieve()
                          .toEntity(TeamsImportWrapper.class)
                          .getBody()
            ),
            wrapper -> wrapper != null ? wrapper.teams() : List.of(),
            req -> {
                Team entity = repo.findByTeamCode(req.teamCode())
                                  .orElseGet(Team::new);
                mapper.request(req, entity);
                result.add(repo.save(entity));
            },
            importProperties.getPageSize()
        );

        return result;
    }

    public List<Team> importTeamsForYear(int year) {
        List<Team> result = new ArrayList<>();

        paginatedImport(
            offset -> fetch(() ->
                restClient.get()
                          .uri("/" + year + "/teams?limit=" + importProperties.getPageSize() + "&offset=" + offset)
                          .retrieve()
                          .toEntity(TeamsImportWrapper.class)
                          .getBody()
            ),
            wrapper -> wrapper != null ? wrapper.teams() : List.of(),
            req -> {
                Team entity = repo.findByTeamCode(req.teamCode())
                                  .orElseGet(Team::new);
                mapper.request(req, entity);
                result.add(repo.save(entity));
            },
            importProperties.getPageSize()
        );

        return result;
    }

}
