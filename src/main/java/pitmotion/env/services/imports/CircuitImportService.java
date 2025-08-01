package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.debug.Debug;
import pitmotion.env.entities.Circuit;
import pitmotion.env.entities.Driver;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.Team;
import pitmotion.env.http.requests.imports.CircuitImportRequest;
import pitmotion.env.http.requests.wrappers.CircuitsImportWrapper;
import pitmotion.env.mappers.imports.CircuitImportMapper;
import pitmotion.env.repositories.CircuitRepository;
import pitmotion.env.repositories.DriverRepository;
import pitmotion.env.repositories.DriverSeasonRepository;
import pitmotion.env.repositories.TeamRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("import")
public class CircuitImportService implements EntityImportService<CircuitImportRequest, CircuitsImportWrapper> {

    private final RestClient restClient;
    private final CircuitRepository circuitRepository;
    private final CircuitImportMapper circuitMapper;
    private final DriverRepository driverRepository;
    private final TeamRepository teamRepository;
    private final DriverSeasonRepository driverSeasonRepository;

    public List<Circuit> importCircuits() {
        List<Circuit> result = new ArrayList<>();

        paginatedImport(
            offset -> fetch(() -> restClient.get()
                .uri("/circuits?limit=100&offset=" + offset)
                .retrieve()
                .toEntity(CircuitsImportWrapper.class)
                .getBody()
            ),
            wrapper -> wrapper != null ? wrapper.circuits() : List.of(),
            req -> {
                Circuit entity = circuitRepository.findByCircuitCode(req.circuitCode())
                        .orElseGet(Circuit::new);

                circuitMapper.request(req, entity);

                // Tentative de lien avec DriverSeason (driver + team + année)
                if (req.fastestLapDriverCode() != null
                        && req.fastestLapTeamCode() != null
                        && req.fastestLapYear() != null) {

                    Optional<Driver> driverOpt = driverRepository.findByDriverCode(req.fastestLapDriverCode());
                    Optional<Team> teamOpt = teamRepository.findByTeamCode(req.fastestLapTeamCode());

                    // if (driverOpt.isEmpty()) {
                    //     Debug.logger().dump("Driver introuvable", req.fastestLapDriverCode());
                    // }

                    // if (teamOpt.isEmpty()) {
                    //     Debug.logger().dump("Team introuvable", req.fastestLapTeamCode());
                    // }

                    if (driverOpt.isPresent() && teamOpt.isPresent()) {
                        Optional<DriverSeason> dsOpt = driverSeasonRepository
                            .findByDriverTeamAndYear(driverOpt.get(), teamOpt.get(), req.fastestLapYear());

                        if (dsOpt.isPresent()) {
                            entity.setFastestLapBy(dsOpt.get());
                        }
                        // else {
                        //     Debug.logger().dump(
                        //         "DriverSeason introuvable pour",
                        //         req.fastestLapDriverCode(),
                        //         req.fastestLapTeamCode(),
                        //         req.fastestLapYear()
                        //     );
                        // }
                    }
                }

                Circuit saved = circuitRepository.save(entity);
                result.add(saved);
            },
            100
        );

        return result;
    }
}
