package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.entities.*;
import pitmotion.env.http.requests.imports.DriverSeasonImportRequest;
import pitmotion.env.http.requests.wrappers.DriverSeasonsImportWrapper;
import pitmotion.env.mappers.imports.DriverSeasonImportMapper;
import pitmotion.env.repositories.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("import")
public class DriverSeasonImportService implements EntityImportService<DriverSeasonImportRequest, DriverSeasonsImportWrapper> {

    private final RestClient restClient;
    private final DriverRepository driverRepository;
    private final TeamRepository teamRepository;
    private final TeamSeasonRepository teamSeasonRepository;
    private final DriverSeasonRepository driverSeasonRepository;
    private final DriverSeasonImportMapper mapper;

    public List<DriverSeason> importForChampionship(Championship championship) {
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
                Optional<Driver> driverOpt = driverRepository.findByDriverCode(req.driverId());
                if (driverOpt.isEmpty()) return;

                Optional<Team> teamOpt = teamRepository.findByTeamCode(req.teamId());
                if (teamOpt.isEmpty()) return;

                Optional<TeamSeason> teamSeasonOpt = teamSeasonRepository.findByTeamAndChampionship(teamOpt.get(), championship);
                if (teamSeasonOpt.isEmpty()) return;

                boolean exists = driverSeasonRepository.existsByDriverAndTeamSeason(driverOpt.get(), teamSeasonOpt.get());
                if (exists) return;

                DriverSeason entity = new DriverSeason();
                entity.setDriver(driverOpt.get());
                entity.setTeamSeason(teamSeasonOpt.get());

                mapper.request(req, entity);

                result.add(driverSeasonRepository.save(entity));
            },

            100
        );

        return result;
    }
}
