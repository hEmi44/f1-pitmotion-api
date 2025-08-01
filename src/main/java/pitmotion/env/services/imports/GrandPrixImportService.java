package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.debug.Debug;
import pitmotion.env.entities.*;
import pitmotion.env.enums.SessionType;
import pitmotion.env.http.requests.imports.GpSessionImportRequest;
import pitmotion.env.http.requests.imports.GrandPrixImportRequest;
import pitmotion.env.http.requests.wrappers.GrandPrixImportWrapper;
import pitmotion.env.mappers.imports.GrandPrixImportMapper;
import pitmotion.env.repositories.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("import")
public class GrandPrixImportService implements EntityImportService<GrandPrixImportRequest, GrandPrixImportWrapper> {

    private final RestClient restClient;
    private final GrandPrixRepository grandPrixRepository;
    private final CircuitRepository circuitRepository;
    private final ChampionshipRepository championshipRepository;
    private final GrandPrixImportMapper mapper;
    private final GpSessionImportService gpSessionImportService;

    public List<GrandPrix> importForChampionship(Championship championship) {
        List<GrandPrix> result = new ArrayList<>();
        String year = String.valueOf(championship.getYear());

        paginatedImport(
            offset -> restClient.get()
                .uri("/" + year + "?limit=100&offset=" + offset)
                .retrieve()
                .toEntity(GrandPrixImportWrapper.class)
                .getBody(),

            wrapper -> wrapper != null && wrapper.races() != null ? wrapper.races() : List.of(),

            req -> {
                Optional<Circuit> circuitOpt = circuitRepository.findByCircuitCode(req.circuit().circuitId());
                Optional<Championship> champOpt = championshipRepository.findByChampionshipCode(req.championshipId());

                if (circuitOpt.isEmpty() || champOpt.isEmpty()) {
                    Debug.logger().dump("❌ Circuit ou championnat introuvable", req.circuit().circuitId(), req.championshipId());
                    return;
                }

                GrandPrix entity = grandPrixRepository.findByGrandPrixCode(req.code()).orElse(new GrandPrix());
                entity.setCircuit(circuitOpt.get());
                entity.setChampionship(champOpt.get());

                mapper.request(req, entity);

                List<GpSessionImportRequest> sessions = req.sessions();
                if (sessions != null && !sessions.isEmpty()) {
                    List<LocalDate> dates = sessions.stream()
                        .filter(s -> s.date() != null)
                        .map(GpSessionImportRequest::date)
                        .toList();

                    entity.setStartingDate(dates.stream().min(LocalDate::compareTo).orElse(null));
                    entity.setEndingDate(dates.stream().max(LocalDate::compareTo).orElse(null));

                    List<LocalTime> times = sessions.stream()
                        .filter(s -> s.time() != null)
                        .map(GpSessionImportRequest::time)
                        .toList();

                    entity.setStartingTime(times.stream().min(LocalTime::compareTo).orElse(null));

                    sessions.stream()
                        .filter(s -> s.type() == SessionType.RACE && s.time() != null)
                        .findFirst()
                        .ifPresent(raceSession -> entity.setEndingTime(raceSession.time().plusHours(3)));
                } else {
                    Debug.logger().dump("⚠️ Sessions vides ou nulles pour ce GP", req.code());
                }

                GrandPrix saved = grandPrixRepository.save(entity);
                int safeLaps = req.laps() != null ? req.laps() : 0;
                gpSessionImportService.importSessionsForGrandPrix(saved, req.sessions(), safeLaps);
                result.add(saved);
            },

            100
        );

        return result;

    }
}
