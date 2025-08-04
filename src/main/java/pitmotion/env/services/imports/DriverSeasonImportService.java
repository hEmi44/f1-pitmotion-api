package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import pitmotion.env.entities.Championship;
import pitmotion.env.entities.Driver;
import pitmotion.env.entities.DriverAlias;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.Team;
import pitmotion.env.entities.TeamSeason;
import pitmotion.env.http.requests.imports.DriverSeasonImportRequest;
import pitmotion.env.http.requests.wrappers.DriverSeasonsImportWrapper;
import pitmotion.env.mappers.imports.DriverSeasonImportMapper;
import pitmotion.env.repositories.DriverAliasRepository;
import pitmotion.env.repositories.DriverRepository;
import pitmotion.env.repositories.DriverSeasonRepository;
import pitmotion.env.repositories.TeamRepository;
import pitmotion.env.repositories.TeamSeasonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("import")
public class DriverSeasonImportService implements EntityImportService<DriverSeasonImportRequest, DriverSeasonsImportWrapper> {

    private final RestClient restClient;
    private final DriverRepository driverRepository;
    private final DriverAliasRepository aliasRepository;
    private final DriverAliasImportService aliasImportService;
    private final TeamRepository teamRepository;
    private final TeamSeasonRepository teamSeasonRepository;
    private final DriverSeasonRepository driverSeasonRepository;
    private final DriverSeasonImportMapper mapper;

    public List<DriverSeason> importForChampionship(Championship championship) {
        aliasImportService.importAllAliases();

        List<DriverSeason> result = new ArrayList<>();
        String year = String.valueOf(championship.getYear());

        paginatedImport(
            offset -> restClient.get()
                .uri("/" + year + "/drivers-championship")
                .retrieve()
                .toEntity(DriverSeasonsImportWrapper.class)
                .getBody(),

            wrapper -> {
                if (wrapper == null || wrapper.drivers() == null) return List.of();
                if (!championship.getChampionshipCode().equals(wrapper.championshipId())) return List.of();
                return wrapper.drivers();
            },

            req -> {
                String rawCode = req.driverId();

                Optional<Driver> driverOpt = driverRepository.findByDriverCode(rawCode);
                if (driverOpt.isEmpty()) {
                    driverOpt = aliasRepository.findByAlias(rawCode)
                                               .map(DriverAlias::getDriver);
                }
                if (driverOpt.isEmpty()) return;
                Driver driver = driverOpt.get();

                boolean isNewAlias = driverRepository.findByDriverCode(rawCode).isEmpty()
                                  && aliasRepository.findByAlias(rawCode).isEmpty();
                if (isNewAlias) {
                    DriverAlias alias = new DriverAlias();
                    alias.setAlias(rawCode);
                    alias.setDriver(driver);
                    aliasRepository.save(alias);
                }

                Optional<Team> teamOpt = teamRepository.findByTeamCode(req.teamId());
                if (teamOpt.isEmpty()) return;
                Optional<TeamSeason> teamSeasonOpt =
                    teamSeasonRepository.findByTeamAndChampionship(teamOpt.get(), championship);
                if (teamSeasonOpt.isEmpty()) return;

                boolean exists = driverSeasonRepository
                    .existsByDriverAndTeamSeason(driver, teamSeasonOpt.get());
                if (exists) return;

                DriverSeason entity = new DriverSeason();
                entity.setDriver(driver);
                entity.setTeamSeason(teamSeasonOpt.get());
                mapper.request(req, entity);
                result.add(driverSeasonRepository.save(entity));
            },

            100
        );

        return result;
    }
}
