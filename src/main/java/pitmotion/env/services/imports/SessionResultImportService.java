package pitmotion.env.services.imports;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.debug.Debug;
import pitmotion.env.entities.Driver;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.GpSession;
import pitmotion.env.entities.SessionResult;
import pitmotion.env.entities.Team;
import pitmotion.env.enums.SessionType;
import pitmotion.env.http.requests.imports.SessionResultImportRequest;
import pitmotion.env.http.requests.wrappers.SessionResultsImportWrapper;
import pitmotion.env.mappers.imports.SessionResultImportMapper;
import pitmotion.env.repositories.DriverRepository;
import pitmotion.env.repositories.DriverSeasonRepository;
import pitmotion.env.repositories.GpSessionRepository;
import pitmotion.env.repositories.SessionResultRepository;
import pitmotion.env.repositories.TeamRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("import")
public class SessionResultImportService implements EntityImportService<SessionResultImportRequest, SessionResultsImportWrapper> {

    private final RestClient restClient;
    private final DriverRepository driverRepository;
    private final TeamRepository teamRepository;
    private final DriverSeasonRepository driverSeasonRepository;
    private final SessionResultRepository sessionResultRepository;
    private final GpSessionRepository gpSessionRepository;
    private final SessionResultImportMapper mapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<SessionResult> importForGpSession(GpSession gpSession) {
        LocalDateTime sessionDateTime = LocalDateTime.of(gpSession.getDate(), gpSession.getTime());
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Brussels"));
        if (sessionDateTime.isAfter(now)) {
            Debug.logger().dump(
                "⚠️ Session skipped (future)",
                "gpSessionId=" + gpSession.getId(),
                "type=" + gpSession.getType().name()
            );
            return Collections.emptyList();
        }

        List<SessionResult> result = new ArrayList<>();
        int year = gpSession.getGrandPrix().getChampionship().getYear();
        int round = gpSession.getGrandPrix().getRound();
        SessionType sessType = gpSession.getType();
        int limit = 100;

        paginatedImport(
            offset -> {
                String path = sessType.getPath();
                String uri = String.format("/%d/%d/%s?limit=%d&offset=%d", year, round, path, limit, offset);
                String raw = fetch(() ->
                    restClient.get()
                              .uri(uri)
                              .retrieve()
                              .toEntity(String.class)
                              .getBody()
                );
                try {
                    return objectMapper.readValue(raw, SessionResultsImportWrapper.class);
                } catch (Exception e) {
                    Debug.logger().dump("❌ Erreur parsing JSON", e.getMessage());
                    return null;
                }
            },
            wrapper -> {
                if (wrapper == null) return List.of();
                return wrapper.resultsForType(sessType);
            },
            req -> {
                if (req == null) return;

                Optional<Driver> drvOpt = driverRepository.findByDriverCode(req.driverId());
                if (drvOpt.isEmpty()) {
                    Debug.logger().dump("❌ Driver non trouvé", "driver=" + req.driverId());
                    return;
                }
                Driver driver = drvOpt.get();

                Optional<Team> teamOpt = teamRepository.findByTeamCode(req.teamId());
                Team team = teamOpt.orElse(null);
                if (team == null) {
                    Debug.logger().dump("⚠️ Team non trouvée (sera ignorée pour liaison)", "team=" + req.teamId());
                }

                Optional<DriverSeason> dsOpt = driverSeasonRepository.findByDriverAndYear(driver, year);
                
                if (dsOpt.isEmpty() && team != null) {
                    dsOpt = driverSeasonRepository.findByDriverTeamAndYear(driver, team, year);
                }
                if (dsOpt.isEmpty()) {
                    Debug.logger().dump(
                        "❌ DriverSeason introuvable",
                        "driver=" + driver.getDriverCode(),
                        team != null ? "team=" + team.getTeamCode() : "team=<none>",
                        "year=" + year
                    );
                    return;
                }
                DriverSeason ds = dsOpt.get();

                SessionResult entity = sessionResultRepository
                    .findByGpSessionAndDriverSeason(gpSession, ds)
                    .orElseGet(SessionResult::new);
                mapper.request(req, entity, gpSession, ds);
                sessionResultRepository.save(entity);
                result.add(entity);
            },
            limit
        );

        result.stream()
        .filter(r -> r.getLapTimeSeconds() != null)
        .min(Comparator.comparingInt(SessionResult::getLapTimeSeconds))
        .ifPresent(fastest -> {
            gpSession.setFastLapBy(fastest.getDriverSeason());
            gpSessionRepository.save(gpSession);
        });

        return result;
    }
}
