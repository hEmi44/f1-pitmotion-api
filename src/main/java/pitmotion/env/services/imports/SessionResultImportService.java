package pitmotion.env.services.imports;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.debug.Debug;
import pitmotion.env.entities.Driver;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.GpSession;
import pitmotion.env.entities.SessionResult;
import pitmotion.env.enums.SessionType;
import pitmotion.env.http.requests.imports.SessionResultImportRequest;
import pitmotion.env.http.requests.wrappers.SessionResultsImportWrapper;
import pitmotion.env.mappers.imports.SessionResultImportMapper;
import pitmotion.env.repositories.DriverRepository;
import pitmotion.env.repositories.DriverSeasonRepository;
import pitmotion.env.repositories.GpSessionRepository;
import pitmotion.env.repositories.SessionResultRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SessionResultImportService extends EntityImportService<SessionResultImportRequest, SessionResultsImportWrapper> {

    private final RestClient restClient;
    private final DriverRepository driverRepository;    
    private final DriverSeasonRepository driverSeasonRepository;
    private final SessionResultRepository sessionResultRepository;
    private final GpSessionRepository gpSessionRepository;
    private final SessionResultImportMapper mapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<SessionResult> importForGpSession(GpSession gpSession) {
        LocalDateTime sessionDateTime = LocalDateTime.of(gpSession.getDate(), gpSession.getTime());
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Brussels"));
        if (sessionDateTime.isAfter(now)) {
            Debug.logger().dump("⚠️ Session skipped (future)", "gpSessionId=" + gpSession.getId());
            return Collections.emptyList();
        }

        List<SessionResult> result = new ArrayList<>();
        int year = gpSession.getGrandPrix().getChampionship().getYear();
        int round = gpSession.getGrandPrix().getRound();
        SessionType sessType = gpSession.getType();

        paginatedImport(
            offset -> {
                String uri = String.format("/%d/%d/%s?limit=%d&offset=%d",
                        year, round, sessType.getPath(), importProperties.getPageSize(), offset);
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
            wrapper -> wrapper != null ? wrapper.resultsForType(sessType) : List.of(),
            req -> {
                if (req == null) return;
                Optional<Driver> drvOpt = driverRepository.findByDriverCode(req.driverId());
                if (drvOpt.isEmpty()) {
                    Debug.logger().dump("❌ Driver non trouvé", "driver=" + req.driverId());
                    return;
                }
                Driver driver = drvOpt.get();

                Optional<DriverSeason> dsOpt = driverSeasonRepository.findByDriverAndYear(driver, year);
                Optional<SessionResult> existing = sessionResultRepository
                    .findByGpSessionAndDriverSeason(gpSession, dsOpt.orElse(null));

                SessionResult entity = existing.orElseGet(SessionResult::new);
                mapper.request(req, entity, gpSession, dsOpt.orElse(null));
                sessionResultRepository.save(entity);
                result.add(entity);
            },
            importProperties.getPageSize()
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
