package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.entities.Circuit;
import pitmotion.env.enums.EntityType;
import pitmotion.env.http.requests.imports.CircuitImportRequest;
import pitmotion.env.http.requests.wrappers.CircuitsImportWrapper;
import pitmotion.env.mappers.imports.CircuitImportMapper;
import pitmotion.env.repositories.AliasRepository;
import pitmotion.env.repositories.CircuitRepository;
import pitmotion.env.repositories.DriverRepository;
import pitmotion.env.repositories.DriverSeasonRepository;
import pitmotion.env.repositories.TeamRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CircuitImportService
    extends EntityImportService<CircuitImportRequest, CircuitsImportWrapper> {

    private final RestClient restClient;
    private final CircuitRepository circuitRepository;
    private final AliasRepository aliasRepository;
    private final CircuitImportMapper circuitMapper;
    private final DriverRepository driverRepository;
    private final TeamRepository teamRepository;
    private final DriverSeasonRepository driverSeasonRepository;
    private final AliasImportService aliasImportService;

    public List<Circuit> importCircuits() {
        List<Circuit> result       = new ArrayList<>();
        List<Pair<String, Long>> newAliases = new ArrayList<>();

        paginatedImport(
            offset -> fetch(() ->
                restClient.get()
                          .uri("/circuits?limit=" + importProperties.getPageSize()
                               + "&offset=" + offset)
                          .retrieve()
                          .toEntity(CircuitsImportWrapper.class)
                          .getBody()
            ),
            wrapper -> wrapper != null ? wrapper.circuits() : List.of(),
            req -> processCircuit(req, result, newAliases),
            importProperties.getPageSize()
        );

        aliasImportService.saveNewAliases(EntityType.CIRCUIT, newAliases);
        return result;
    }


    private void processCircuit(CircuitImportRequest req,
                                List<Circuit> result,
                                List<Pair<String, Long>> newAliases) {
        String incomingCode  = req.circuitCode();
        String normalizedUrl = normalizeUrl(req.url());
        String incomingSlug  = slugify(req.name() + " " + req.city());

        Optional<Circuit> byCode = circuitRepository.findByCircuitCode(incomingCode);
        Optional<Circuit> byAlias = byCode.isEmpty()
            ? aliasRepository
                  .findByEntityTypeAndAlias(EntityType.CIRCUIT, incomingCode)
                  .flatMap(a -> circuitRepository.findById(a.getEntityId()))
            : Optional.empty();

        Optional<Circuit> byUrl = byCode.isEmpty() && byAlias.isEmpty() && normalizedUrl != null
            ? circuitRepository.findByUrl(normalizedUrl)
            : Optional.empty();

        Optional<Circuit> bySlug = byCode.isEmpty() && byAlias.isEmpty() && byUrl.isEmpty()
            ? circuitRepository.findAll().stream()
                .filter(c -> slugify(c.getName() + " " + c.getCity()).equals(incomingSlug))
                .findFirst()
            : Optional.empty();

        Optional<Circuit> byFuzzy = byCode.isEmpty() && byAlias.isEmpty()
            && byUrl.isEmpty() && bySlug.isEmpty()
            ? findFuzzyMatch(
                  incomingSlug,
                  circuitRepository.findAll(),
                  c -> slugify(c.getName() + " " + c.getCity())
              )
            : Optional.empty();

        Optional<Circuit> existingOpt = byCode
            .or(() -> byAlias)
            .or(() -> byUrl)
            .or(() -> bySlug)
            .or(() -> byFuzzy);

        Circuit entity;
        String oldCode = null;
        if (existingOpt.isPresent()) {
            entity  = existingOpt.get();
            oldCode = entity.getCircuitCode();
        } else {
            entity = new Circuit();
        }

        circuitMapper.request(req, entity);
        if (oldCode != null) {
            entity.setCircuitCode(oldCode);
        }

        if (req.fastestLapDriverCode() != null
         && req.fastestLapTeamCode()   != null
         && req.fastestLapYear()       != null) {

            driverRepository.findByDriverCode(req.fastestLapDriverCode())
                .flatMap(d -> teamRepository.findByTeamCode(req.fastestLapTeamCode())
                    .flatMap(t -> driverSeasonRepository
                        .findByDriverTeamAndYear(d, t, req.fastestLapYear())))
                .ifPresent(entity::setFastestLapBy);
        }

        Circuit saved = circuitRepository.save(entity);
        result.add(saved);

        if (oldCode != null && !oldCode.equals(incomingCode)) {
            newAliases.add(Pair.of(incomingCode, saved.getId()));
        }
    }
}
