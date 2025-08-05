package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.debug.Debug;
import pitmotion.env.entities.Circuit;
import pitmotion.env.entities.CircuitAlias;
import pitmotion.env.entities.GrandPrix;
import pitmotion.env.entities.Championship;
import pitmotion.env.http.requests.imports.GrandPrixImportRequest;
import pitmotion.env.http.requests.wrappers.GrandPrixImportWrapper;
import pitmotion.env.mappers.imports.GrandPrixImportMapper;
import pitmotion.env.repositories.CircuitAliasRepository;
import pitmotion.env.repositories.CircuitRepository;
import pitmotion.env.repositories.GrandPrixRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GrandPrixImportService extends EntityImportService<GrandPrixImportRequest, GrandPrixImportWrapper> {

    private final RestClient restClient;
    private final GrandPrixRepository repo;
    private final CircuitRepository circuitRepo;
    private final CircuitAliasRepository aliasRepo;
    private final GrandPrixImportMapper mapper;
    private final GpSessionImportService sessionService;

    public List<GrandPrix> importForChampionship(Championship championship) {
        List<GrandPrix> result = new ArrayList<>();
        String year = String.valueOf(championship.getYear());

        paginatedImport(
            offset -> fetch(() ->
                restClient.get()
                          .uri("/" + year + "?limit=" + importProperties.getPageSize() + "&offset=" + offset)
                          .retrieve()
                          .toEntity(GrandPrixImportWrapper.class)
                          .getBody()
            ),
            wrapper -> wrapper != null && wrapper.races() != null ? wrapper.races() : List.of(),
            req -> {
                String circuitCode = req.circuit().circuitId();

                Optional<Circuit> circuitOpt = circuitRepo.findByCircuitCode(circuitCode)
                    .or(() -> aliasRepo.findByAlias(circuitCode).map(CircuitAlias::getCircuit));

                if (circuitOpt.isEmpty()) {
                    Debug.logger().dump("❌ Circuit introuvable", circuitCode);
                    return;
                }

                GrandPrix entity = repo.findByGrandPrixCode(req.code()).orElse(new GrandPrix());
                entity.setCircuit(circuitOpt.get());
                entity.setChampionship(championship);
                mapper.request(req, entity);

                if (req.sessions() != null && !req.sessions().isEmpty()) {
                    List<LocalDate> dates = req.sessions().stream()
                        .map(s -> s.date())
                        .toList();
                    entity.setStartingDate(dates.stream().min(LocalDate::compareTo).orElse(null));
                    entity.setEndingDate(dates.stream().max(LocalDate::compareTo).orElse(null));

                    List<LocalTime> times = req.sessions().stream()
                        .map(s -> s.time())
                        .toList();
                    entity.setStartingTime(times.stream().min(LocalTime::compareTo).orElse(null));
                } else {
                    Debug.logger().dump("⚠️ Sessions vides", req.code());
                }

                GrandPrix saved = repo.save(entity);
                sessionService.importSessionsForGrandPrix(
                    saved,
                    req.sessions(),
                    req.laps() != null ? req.laps() : 0
                );
                result.add(saved);
            },
            importProperties.getPageSize()
        );

        return result;
    }

}
