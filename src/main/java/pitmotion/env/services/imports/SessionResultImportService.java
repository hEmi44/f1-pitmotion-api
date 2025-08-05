package pitmotion.env.services.imports;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
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
public class SessionResultImportService
    extends EntityImportService<SessionResultImportRequest, SessionResultsImportWrapper> {

    private final RestClient restClient;
    private final DriverRepository driverRepo;
    private final DriverSeasonRepository dsRepo;
    private final SessionResultRepository resultRepo;
    private final GpSessionRepository sessionRepo;
    private final SessionResultImportMapper mapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<SessionResult> importForGpSession(GpSession gp) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Europe/Brussels"));
        if (LocalDateTime.of(gp.getDate(), gp.getTime()).isAfter(now)) {
            return Collections.emptyList();
        }

        List<SessionResult> results = new ArrayList<>();
        int year = gp.getGrandPrix().getChampionship().getYear();
        int round = gp.getGrandPrix().getRound();
        SessionType type = gp.getType();

        paginatedImport(
            offset -> fetch(() -> {
                String uri = String.format("/%d/%d/%s?limit=%d&offset=%d",
                    year, round, type.getPath(), importProperties.getPageSize(), offset);
                String raw = restClient.get().uri(uri).retrieve().toEntity(String.class).getBody();
                try {
                    return objectMapper.readValue(raw, SessionResultsImportWrapper.class);
                } catch (Exception e) {
                    return null;
                }
            }),
            wrapper -> wrapper != null ? wrapper.resultsForType(type) : List.of(),
            req -> processSessionResult(req, gp, year, results),
            importProperties.getPageSize()
        );

        results.stream()
               .filter(r -> r.getLapTimeSeconds() != null)
               .min(Comparator.comparingInt(SessionResult::getLapTimeSeconds))
               .ifPresent(fastest -> {
                   gp.setFastLapBy(fastest.getDriverSeason());
                   sessionRepo.save(gp);
               });

        return results;
    }

    private void processSessionResult(SessionResultImportRequest req,
                                      GpSession gp,
                                      int year,
                                      List<SessionResult> results) {
        if (req == null) return;
        Optional<Driver> drv = driverRepo.findByDriverCode(req.driverId());
        if (drv.isEmpty()) return;
        Optional<DriverSeason> ds = dsRepo.findByDriverAndYear(drv.get(), year);

        SessionResult entity = resultRepo
            .findByGpSessionAndDriverSeason(gp, ds.orElse(null))
            .orElseGet(SessionResult::new);

        mapper.request(req, entity, gp, ds.orElse(null));
        resultRepo.save(entity);
        results.add(entity);
    }
}
