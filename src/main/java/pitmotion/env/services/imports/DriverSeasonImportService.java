package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.entities.Championship;
import pitmotion.env.entities.Driver;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.Team;
import pitmotion.env.enums.EntityType;
import pitmotion.env.http.requests.imports.DriverSeasonImportRequest;
import pitmotion.env.http.requests.wrappers.DriverSeasonsImportWrapper;
import pitmotion.env.mappers.imports.DriverSeasonImportMapper;
import pitmotion.env.repositories.AliasRepository;
import pitmotion.env.repositories.DriverRepository;
import pitmotion.env.repositories.DriverSeasonRepository;
import pitmotion.env.repositories.TeamRepository;
import pitmotion.env.repositories.TeamSeasonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DriverSeasonImportService
    extends EntityImportService<DriverSeasonImportRequest, DriverSeasonsImportWrapper> {

    private final RestClient restClient;
    private final DriverRepository driverRepository;
    private final AliasRepository aliasRepository;
    private final TeamRepository teamRepository;
    private final TeamSeasonRepository teamSeasonRepository;
    private final DriverSeasonRepository driverSeasonRepository;
    private final DriverSeasonImportMapper mapper;
    private final AliasImportService aliasImportService;

    public List<DriverSeason> importForChampionship(Championship championship) {
        List<DriverSeason> result       = new ArrayList<>();
        List<Pair<String, Long>> newAliases = new ArrayList<>();
        String year = String.valueOf(championship.getYear());

        paginatedImport(
            offset -> fetch(() ->
                restClient.get()
                          .uri("/" + year + "/drivers-championship?limit="
                               + importProperties.getPageSize()
                               + "&offset=" + offset)
                          .retrieve()
                          .toEntity(DriverSeasonsImportWrapper.class)
                          .getBody()
            ),
            wrapper -> {
                if (wrapper == null || wrapper.drivers() == null) {
                    return List.<DriverSeasonImportRequest>of();
                }
                if (!championship.getChampionshipCode().equals(wrapper.championshipId())) {
                    return List.<DriverSeasonImportRequest>of();
                }
                return wrapper.drivers();
            },
            req -> processDriverSeason(req, championship, result, newAliases),
            importProperties.getPageSize()
        );

        aliasImportService.saveNewAliases(EntityType.DRIVER, newAliases);
        return result;
    }

    private void processDriverSeason(DriverSeasonImportRequest req,
                                     Championship championship,
                                     List<DriverSeason> result,
                                     List<Pair<String, Long>> newAliases) {
        String rawCode = req.driverId();

        Optional<Driver> byCode = driverRepository.findByDriverCode(rawCode);
        Optional<Driver> byAlias = byCode.isEmpty()
            ? aliasRepository.findByEntityTypeAndAlias(EntityType.DRIVER, rawCode)
                             .flatMap(a -> driverRepository.findById(a.getEntityId()))
            : Optional.empty();

        Optional<Driver> driverOpt = byCode.or(() -> byAlias);
        if (driverOpt.isEmpty()) {
            return;
        }
        Driver driver = driverOpt.get();

        boolean isNewAlias = byCode.isEmpty() && byAlias.isEmpty();
        if (isNewAlias) {
            newAliases.add(Pair.of(rawCode, driver.getId()));
        }

        Optional<Team> teamOpt = teamRepository.findByTeamCode(req.teamId());
        if (teamOpt.isEmpty()) {
            return;
        }
        Optional<?> teamSeasonOpt =
            teamSeasonRepository.findByTeamAndChampionship(teamOpt.get(), championship);
        if (teamSeasonOpt.isEmpty()) {
            return;
        }

        boolean exists = driverSeasonRepository
            .existsByDriverAndTeamSeason(driver, (pitmotion.env.entities.TeamSeason)teamSeasonOpt.get());
        if (exists) {
            return;
        }

        DriverSeason entity = new DriverSeason();
        entity.setDriver(driver);
        entity.setTeamSeason((pitmotion.env.entities.TeamSeason)teamSeasonOpt.get());
        mapper.request(req, entity);

        result.add(driverSeasonRepository.save(entity));
    }
}
