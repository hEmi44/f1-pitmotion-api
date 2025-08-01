package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
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
@Profile("import")
public class TeamImportService implements EntityImportService<TeamImportRequest, TeamsImportWrapper> {

    private final RestClient restClient;
    private final TeamRepository teamRepository;
    private final TeamImportMapper teamMapper;

    public List<Team> importTeams() {
        List<Team> result = new ArrayList<>();
    
        paginatedImport(
            offset -> fetch(() -> restClient.get()
                .uri("/teams?limit=100&offset=" + offset)
                .retrieve()
                .toEntity(TeamsImportWrapper.class)
                .getBody()),
            wrapper -> wrapper != null ? wrapper.teams() : List.of(),
            req -> {
                Team entity = teamRepository.findByTeamCode(req.teamCode())
                        .orElseGet(Team::new);
                teamMapper.request(req, entity);
                Team saved = teamRepository.save(entity);
                result.add(saved);
            },
            100
        );
    
        return result;
    }

    public List<Team> importTeamsForYear(int year) {
        List<Team> result = new ArrayList<>();
    
        paginatedImport(
            offset -> restClient.get()
                .uri("/" + year + "/teams?limit=100&offset=" + offset)
                .retrieve()
                .toEntity(TeamsImportWrapper.class)
                .getBody(),
            wrapper -> wrapper != null ? wrapper.teams() : List.of(),
            req -> {
                Team entity = teamRepository.findByTeamCode(req.teamCode())
                    .orElseGet(Team::new);
                teamMapper.request(req, entity);
                result.add(teamRepository.save(entity));
            },
            100
        );
    
        return result;
    }
    
    
}
