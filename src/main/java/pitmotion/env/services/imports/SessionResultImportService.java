package pitmotion.env.services.imports;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.entities.Driver;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.GpSession;
import pitmotion.env.entities.SessionResult;
import pitmotion.env.entities.Team;
import pitmotion.env.http.requests.imports.SessionResultImportRequest;
import pitmotion.env.http.requests.wrappers.SessionResultsImportWrapper;
import pitmotion.env.mappers.imports.SessionResultImportMapper;
import pitmotion.env.repositories.DriverRepository;
import pitmotion.env.repositories.DriverSeasonRepository;
import pitmotion.env.repositories.GpSessionRepository;
import pitmotion.env.repositories.SessionResultRepository;
import pitmotion.env.repositories.TeamRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("import")
public class SessionResultImportService implements EntityImportService<SessionResultImportRequest, SessionResultsImportWrapper> {

    private final RestClient restClient;
    private final DriverRepository        driverRepository;
    private final TeamRepository          teamRepository;
    private final DriverSeasonRepository  driverSeasonRepository;
    private final SessionResultRepository sessionResultRepository;
    private final GpSessionRepository     gpSessionRepository;
    private final SessionResultImportMapper mapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<SessionResult> importForGpSession(GpSession gpSession) {
        List<SessionResult> result = new ArrayList<>();
        int year  = gpSession.getGrandPrix().getChampionship().getYear();
        int round = gpSession.getGrandPrix().getRound();

        String rawType = gpSession.getType().name();
        String key;
        switch (rawType) {
            case "FP1":               key = "fp1";            break;
            case "FP2":               key = "fp2";            break;
            case "FP3":               key = "fp3";            break;
            case "QUALIFYING":
            case "QUALY":             key = "qualy";          break;
            case "RACE":              key = "race";           break;
            case "SPRINT_QUALIFYING":
            case "SPRINT_QUALY":      key = "sprint/qualy";   break;
            case "SPRINT_RACE":       key = "sprint/race";    break;
            default:
                return result;
        }

        int limit = 100;
        paginatedImport(
            offset -> {
                String rawJson = fetch(() ->
                    restClient.get()
                        .uri("/{y}/{r}/" + key + "?limit=" + limit + "&offset=" + offset, year, round)
                        .retrieve()
                        .toEntity(String.class)
                        .getBody()
                );
                try {
                    return objectMapper.readValue(rawJson, SessionResultsImportWrapper.class);
                } catch (Exception e) {
                    return null;
                }
            },

            wrapper -> wrapper != null ? wrapper.getResults() : List.of(),

            req -> {
                if (req == null) return;

                Optional<Driver> driverOpt = driverRepository.findByDriverCode(req.driverId());
                if (driverOpt.isEmpty()) return;

                Optional<Team> teamOpt = teamRepository.findByTeamCode(req.teamId());
                if (teamOpt.isEmpty()) return;

                Optional<DriverSeason> dsOpt = driverSeasonRepository
                    .findByDriverTeamAndYear(driverOpt.get(), teamOpt.get(), year);
                if (dsOpt.isEmpty()) return;

                SessionResult entity = sessionResultRepository
                    .findByGpSessionAndDriverSeason(gpSession, dsOpt.get())
                    .orElse(new SessionResult());

                mapper.request(req, entity, gpSession, dsOpt.get());
                sessionResultRepository.save(entity);
                result.add(entity);
            },

            limit
        );

        result.stream()
        .filter(r -> r.getLapTimeSeconds() != null)
        .min(Comparator.comparingInt(SessionResult::getLapTimeSeconds))
        .ifPresent(fastest -> {
            gpSession.setFastLapBy(fastest.getDriverSeason().getDriver());
            gpSessionRepository.save(gpSession);
        });

  return result;
    }
}
