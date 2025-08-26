package pitmotion.env.mappers;

import java.util.*;

import org.springframework.stereotype.Component;

import pitmotion.env.http.resources.teams.TeamDriverYearResource;
import pitmotion.env.http.resources.teams.TeamListItemResource;
import pitmotion.env.http.resources.teams.TeamListResource;
import pitmotion.env.http.resources.teams.TeamResource;
import pitmotion.env.entities.Driver;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.Team;
import pitmotion.env.entities.TeamSeason;

@Component
public class TeamMapper {

  public TeamListResource toTeamListResource(
      List<Team> teams,
      Integer referenceYear,
      Map<Long, List<DriverSeason>> driversByTeamForRefYear) {

    List<TeamListItemResource> items = teams.stream()
        .map(t -> toListItem(t, driversByTeamForRefYear.getOrDefault(t.getId(), List.of())))
        .toList();

    return new TeamListResource(referenceYear, items.size(), items);
  }

  public TeamResource toTeamResource(Team team, Map<Integer, List<DriverSeason>> driverSeasonsByYear) {
    List<Integer> years = new ArrayList<>(driverSeasonsByYear.keySet());
    years.sort(Comparator.reverseOrder());

    List<TeamDriverYearResource> lastDrivers = years.stream()
        .limit(5)
        .flatMap(y -> driverSeasonsByYear.get(y).stream().map(this::toTeamDriverYear))
        .filter(Objects::nonNull)
        .toList();

    return new TeamResource(
      team.getTeamCode(),
      team.getName(),
      team.getCountry() != null ? team.getCountry().getCodeIso3() : null,
      team.getFirstAppearance(),
      lastDrivers
    );
  }

  private TeamListItemResource toListItem(Team t, List<DriverSeason> seasonsForYear) {
    List<TeamDriverYearResource> currentDrivers = seasonsForYear.stream()
        .map(this::toTeamDriverYear)
        .filter(Objects::nonNull)
        .toList();

    return new TeamListItemResource(
      t.getTeamCode(),
      t.getName(),
      currentDrivers
    );
  }

  private TeamDriverYearResource toTeamDriverYear(DriverSeason ds) {
    if (ds == null) return null;
    TeamSeason ts = ds.getTeamSeason();
    Integer year = ts != null && ts.getChampionship() != null ? ts.getChampionship().getYear() : null;
    Driver d = ds.getDriver();
    return new TeamDriverYearResource(
      year,
      d != null ? d.getDriverCode() : null,
      d != null ? d.getSurname() : null,
      d != null ? d.getName() : null,
      parseInt(ds.getDriverNumber(), null)
    );
  }

  private Integer parseInt(String s, Integer def) {
    try { return s == null ? def : Integer.parseInt(s); } catch (Exception e) { return def; }
  }
}
