package pitmotion.env.services;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import pitmotion.env.configurations.ImportProperties;
import pitmotion.env.entities.Championship;
import pitmotion.env.entities.Driver;
import pitmotion.env.entities.Team;
import pitmotion.env.entities.Circuit;
import pitmotion.env.http.requests.wrappers.ChampionshipsImportWrapper;
import pitmotion.env.http.requests.wrappers.DriversImportWrapper;
import pitmotion.env.http.requests.wrappers.TeamsImportWrapper;
import pitmotion.env.http.requests.wrappers.CircuitsImportWrapper;
import pitmotion.env.mappers.imports.ChampionshipImportMapper;
import pitmotion.env.mappers.imports.DriverImportMapper;
import pitmotion.env.mappers.imports.TeamImportMapper;
import pitmotion.env.mappers.imports.CircuitImportMapper;
import pitmotion.env.repositories.ChampionshipRepository;
import pitmotion.env.repositories.DriverRepository;
import pitmotion.env.repositories.TeamRepository;
import pitmotion.env.repositories.CircuitRepository;

import java.time.Duration;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
@Profile("import")
public class ImportService {
    private static final Logger log = LoggerFactory.getLogger(ImportService.class);

    private final RestClient restClient;
    private final ChampionshipRepository championshipRepository;
    private final DriverRepository driverRepository;
    private final TeamRepository teamRepository;
    private final CircuitRepository circuitRepository;
    private final ChampionshipImportMapper championshipMapper;
    private final DriverImportMapper driverMapper;
    private final TeamImportMapper teamMapper;
    private final CircuitImportMapper circuitMapper;
    private final ImportProperties importProperties;

    @Transactional
    public void importAll() {
        importChampionships();
        pause(importProperties.getDelayMs());
        importDrivers();
        pause(importProperties.getDelayMs());
        importTeams();
        pause(importProperties.getDelayMs());
        importCircuits();
    }

    private void importChampionships() {
        log.info("Début import championnats");
        ChampionshipsImportWrapper wrapper = executeWithInfiniteBackoff(() -> {
            ResponseEntity<ChampionshipsImportWrapper> resp =
                restClient.get().uri("/seasons").retrieve()
                          .toEntity(ChampionshipsImportWrapper.class);
            return resp.getBody();
        });
        if (wrapper != null && wrapper.championships() != null) {
            wrapper.championships().forEach(req -> {
                Championship ent = championshipMapper.request(req, new Championship());
                championshipRepository.save(ent);
            });
        }
    }

    private void importDrivers() {
        log.info("Début import pilotes");
        DriversImportWrapper wrapper = executeWithInfiniteBackoff(() -> {
            ResponseEntity<DriversImportWrapper> resp =
                restClient.get().uri("/drivers").retrieve()
                          .toEntity(DriversImportWrapper.class);
            return resp.getBody();
        });
        if (wrapper != null && wrapper.drivers() != null) {
            wrapper.drivers().forEach(req -> {
                Driver ent = driverMapper.request(req, new Driver());
                driverRepository.save(ent);
            });
        }
    }

    private void importTeams() {
        log.info("Début import équipes");
        TeamsImportWrapper wrapper = executeWithInfiniteBackoff(() -> {
            ResponseEntity<TeamsImportWrapper> resp =
                restClient.get().uri("/teams").retrieve()
                          .toEntity(TeamsImportWrapper.class);
            return resp.getBody();
        });
        if (wrapper != null && wrapper.teams() != null) {
            wrapper.teams().forEach(req -> {
                Team ent = teamMapper.request(req, new Team());
                teamRepository.save(ent);
            });
        }
    }

    private void importCircuits() {
        log.info("Début import circuits");
        CircuitsImportWrapper wrapper = executeWithInfiniteBackoff(() -> {
            ResponseEntity<CircuitsImportWrapper> resp =
                restClient.get().uri("/circuits").retrieve()
                          .toEntity(CircuitsImportWrapper.class);
            return resp.getBody();
        });
        if (wrapper != null && wrapper.circuits() != null) {
            wrapper.circuits().forEach(req -> {
                Circuit ent = circuitMapper.request(req, new Circuit());
                circuitRepository.save(ent);
            });
        }
    }

    private <T> T executeWithInfiniteBackoff(Supplier<T> call) {
        int attempt = 0;
        while (true) {
            try {
                return call.get();
            } catch (Exception e) {
                Duration delay;
                if (attempt == 0) {
                    delay = Duration.ofSeconds(30);
                } else if (attempt == 1) {
                    delay = Duration.ofMinutes(1);
                } else {
                    delay = Duration.ofMinutes(5);
                }
                log.warn("Appel n°{} échoué ({}). Retry dans {}.", attempt + 1, e.getMessage(), delay);
                try {
                    Thread.sleep(delay.toMillis());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Import interrompu", ie);
                }
                attempt++;
            }
        }
    }

    private void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
