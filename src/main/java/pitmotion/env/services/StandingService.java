package pitmotion.env.services;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pitmotion.env.http.resources.standings.DriverStandingsResource;
import pitmotion.env.http.resources.standings.TeamStandingsResource;
import pitmotion.env.mappers.StandingMapper;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.TeamSeason;
import pitmotion.env.repositories.DriverSeasonRepository;
import pitmotion.env.repositories.TeamSeasonRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StandingService {

  private final DriverSeasonRepository driverSeasonRepository;
  private final TeamSeasonRepository teamSeasonRepository;
  private final StandingMapper mapper;

  public DriverStandingsResource getDriverStandings(int year) {
    List<DriverSeason> rows = driverSeasonRepository.findAll().stream()
        .filter(ds -> ds.getTeamSeason() != null
            && ds.getTeamSeason().getChampionship() != null
            && year == ds.getTeamSeason().getChampionship().getYear())
        .sorted(
            Comparator
                .comparing((DriverSeason ds) -> ds.getStandings() == null ? Integer.MAX_VALUE : ds.getStandings())
                .thenComparing(ds -> ds.getPoints() == null ? 0 : ds.getPoints(), Comparator.reverseOrder())
        )
        .toList();

    if (rows.isEmpty()) throw new EntityNotFoundException("Classement pilotes indisponible pour " + year);

    Integer afterRound = null;
    OffsetDateTime lastUpdated = null;

    return mapper.toDriverStandingsResource(year, afterRound, lastUpdated, rows);
  }

  public TeamStandingsResource getTeamStandings(int year) {
    List<TeamSeason> rows = teamSeasonRepository.findAll().stream()
        .filter(ts -> ts.getChampionship() != null && year == ts.getChampionship().getYear())
        .sorted(
            Comparator
                .comparing((TeamSeason ts) -> ts.getStandings() == null ? Integer.MAX_VALUE : ts.getStandings())
                .thenComparing(ts -> ts.getPoints() == null ? 0 : ts.getPoints(), Comparator.reverseOrder())
        )
        .toList();

    if (rows.isEmpty()) throw new EntityNotFoundException("Classement équipes indisponible pour " + year);

    Integer afterRound = null;
    OffsetDateTime lastUpdated = null;

    return mapper.toTeamStandingsResource(year, afterRound, lastUpdated, rows);
  }
}