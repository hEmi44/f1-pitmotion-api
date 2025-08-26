package pitmotion.env.mappers;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import pitmotion.env.http.resources.drivers.*;
import pitmotion.env.entities.*;

@Component
public class DriverMapper {

  public DriverListResource toDriverListResource(
      List<Driver> drivers,
      Integer referenceYear,
      Map<Long, List<DriverSeason>> driverSeasonsIndex) {

    List<DriverListItemResource> items = drivers.stream()
        .map(d -> toListItem(d, referenceYear, driverSeasonsIndex.getOrDefault(d.getId(), List.of())))
        .toList();

    return new DriverListResource(referenceYear, items.size(), items);
  }

  public DriverResource toDriverResource(Driver d, List<DriverSeason> seasonsDesc) {
    List<DriverTeamYearResource> lastTeams = seasonsDesc.stream()
        .map(this::toDriverTeamYear)
        .filter(Objects::nonNull)
        .limit(5)
        .toList();

    return new DriverResource(
      d.getDriverCode(),
      d.getSurname(),
      d.getName(),
      lastTeams.isEmpty() ? null : lastTeams.get(0).number(),
      d.getCountry() != null ? d.getCountry().getCodeIso3() : null,
      d.getBirthday(),
      lastTeams
    );
  }


  private DriverListItemResource toListItem(Driver d, Integer referenceYear, List<DriverSeason> seasons) {
    DriverTeamYearResource current = resolveCurrentSeason(seasons, referenceYear);
    Integer number = current != null ? current.number() : null;

    return new DriverListItemResource(
      d.getDriverCode(),
      d.getSurname(),
      d.getName(),
      number,
      current
    );
  }

  private DriverTeamYearResource toDriverTeamYear(DriverSeason ds) {
    if (ds == null) return null;
    TeamSeason ts = ds.getTeamSeason();
    Team t = ts != null ? ts.getTeam() : null;
    Integer year = (ts != null && ts.getChampionship() != null) ? ts.getChampionship().getYear() : null;

    Integer number = null;
    try { number = ds.getDriverNumber() == null ? null : Integer.parseInt(ds.getDriverNumber()); }
    catch (Exception ignored) {}

    return new DriverTeamYearResource(
      year,
      t != null ? t.getTeamCode() : null,
      t != null ? t.getName() : null,
      number
    );
  }

  private DriverTeamYearResource resolveCurrentSeason(List<DriverSeason> seasons, Integer refYear) {
    if (seasons == null || seasons.isEmpty()) return null;

    Map<Integer, List<DriverSeason>> byYear = seasons.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.groupingBy(ds ->
            ds.getTeamSeason() != null && ds.getTeamSeason().getChampionship() != null
                ? ds.getTeamSeason().getChampionship().getYear()
                : -1
        ));

    Integer chosen = refYear;
    if (chosen == null || !byYear.containsKey(chosen)) {
      chosen = byYear.keySet().stream().filter(y -> y != -1).max(Integer::compareTo).orElse(null);
    }
    if (chosen == null) return null;

    DriverSeason ds = byYear.get(chosen).get(0);
    DriverTeamYearResource dty = toDriverTeamYear(ds);
    return dty != null ? new DriverTeamYearResource(chosen, dty.teamCode(), dty.teamName(), dty.number()) : null;
  }
}
