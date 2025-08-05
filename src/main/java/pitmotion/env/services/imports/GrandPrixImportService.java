package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.entities.Circuit;
import pitmotion.env.entities.GrandPrix;
import pitmotion.env.entities.Championship;
import pitmotion.env.enums.EntityType;
import pitmotion.env.http.requests.imports.GrandPrixImportRequest;
import pitmotion.env.http.requests.wrappers.GrandPrixImportWrapper;
import pitmotion.env.mappers.imports.GrandPrixImportMapper;
import pitmotion.env.repositories.AliasRepository;
import pitmotion.env.repositories.CircuitRepository;
import pitmotion.env.repositories.GrandPrixRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GrandPrixImportService
    extends EntityImportService<GrandPrixImportRequest, GrandPrixImportWrapper> {

    private final RestClient restClient;
    private final GrandPrixRepository gpRepo;
    private final CircuitRepository circuitRepo;
    private final AliasRepository aliasRepo;
    private final GrandPrixImportMapper mapper;
    private final GpSessionImportService sessionService;
    private final AliasImportService aliasImportService;

    public List<GrandPrix> importForChampionship(Championship champ) {
        List<GrandPrix> result = new ArrayList<>();
        List<Pair<String, Long>> newAliases = new ArrayList<>();
        String year = String.valueOf(champ.getYear());

        paginatedImport(
            offset -> fetch(() ->
                restClient.get()
                          .uri("/" + year + "?limit=" + importProperties.getPageSize() + "&offset=" + offset)
                          .retrieve()
                          .toEntity(GrandPrixImportWrapper.class)
                          .getBody()
            ),
            wrapper -> wrapper != null && wrapper.races() != null ? wrapper.races() : List.of(),
            req -> processGrandPrix(req, champ, result, newAliases),
            importProperties.getPageSize()
        );

        aliasImportService.saveNewAliases(EntityType.CIRCUIT, newAliases);
        return result;
    }

    private void processGrandPrix(GrandPrixImportRequest req,
                                  Championship champ,
                                  List<GrandPrix> result,
                                  List<Pair<String, Long>> newAliases) {
        String code = req.circuit().circuitId();

        Optional<Circuit> byCode = circuitRepo.findByCircuitCode(code);
        Optional<Circuit> byAlias = byCode.isEmpty()
            ? aliasRepo.findByEntityTypeAndAlias(EntityType.CIRCUIT, code)
                       .flatMap(a -> circuitRepo.findById(a.getEntityId()))
            : Optional.empty();
        Optional<Circuit> circuitOpt = byCode.or(() -> byAlias);
        if (circuitOpt.isEmpty()) return;
        Circuit circuit = circuitOpt.get();

        if (byCode.isEmpty() && byAlias.isEmpty()) {
            newAliases.add(Pair.of(code, circuit.getId()));
        }

        GrandPrix gp = gpRepo.findByGrandPrixCode(req.code()).orElseGet(GrandPrix::new);
        gp.setCircuit(circuit);
        gp.setChampionship(champ);
        mapper.request(req, gp);

        if (req.sessions() != null && !req.sessions().isEmpty()) {
            List<LocalDate> dates = req.sessions().stream().map(s -> s.date()).toList();
            gp.setStartingDate(dates.stream().min(LocalDate::compareTo).orElse(null));
            gp.setEndingDate(dates.stream().max(LocalDate::compareTo).orElse(null));

            List<LocalTime> times = req.sessions().stream().map(s -> s.time()).toList();
            gp.setStartingTime(times.stream().min(LocalTime::compareTo).orElse(null));
        }

        GrandPrix saved = gpRepo.save(gp);
        sessionService.importSessionsForGrandPrix(saved, req.sessions(), req.laps() == null ? 0 : req.laps());
        result.add(saved);
    }
}
