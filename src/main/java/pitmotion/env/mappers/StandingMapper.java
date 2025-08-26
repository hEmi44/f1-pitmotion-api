package pitmotion.env.mappers;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import pitmotion.env.http.resources.standings.*;
import pitmotion.env.entities.Driver;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.Team;
import pitmotion.env.entities.TeamSeason;

@Component
public class StandingMapper {

  public DriverStandingsResource toDriverStandingsResource(
      int year, Integer afterRound, OffsetDateTime lastUpdated, List<DriverSeason> rows) {

    List<DriverStandingEntryResource> entries = rows.stream()
        .map(this::toDriverStandingEntry)
        .toList();

    return new DriverStandingsResource(year, afterRound, lastUpdated, entries.size(), entries);
  }

  public TeamStandingsResource toTeamStandingsResource(
      int year, Integer afterRound, OffsetDateTime lastUpdated, List<TeamSeason> rows) {

    List<TeamStandingEntryResource> entries = rows.stream()
        .map(this::toTeamStandingEntry)
        .toList();

    return new TeamStandingsResource(year, afterRound, lastUpdated, entries.size(), entries);
  }

  public DriverStandingEntryResource toDriverStandingEntry(DriverSeason ds) {
    Integer position = ds.getStandings() == null ? 0 : ds.getStandings();
    double points = ds.getPoints() == null ? 0d : ds.getPoints().doubleValue();
    int wins = ds.getWins() == null ? 0 : ds.getWins();

    Driver d = ds.getDriver();
    String driverCode = d != null ? d.getDriverCode() : null;
    String firstName  = d != null ? d.getSurname() : null;
    String lastName   = d != null ? d.getName() : null;

    Team t = ds.getTeamSeason() != null ? ds.getTeamSeason().getTeam() : null;
    String teamCode = t != null ? t.getTeamCode() : null;
    String teamName = t != null ? t.getName() : null;

    return new DriverStandingEntryResource(
      position, points, wins,
      driverCode, firstName, lastName,
      teamCode, teamName
    );
  }

  public TeamStandingEntryResource toTeamStandingEntry(TeamSeason ts) {
    Integer position = ts.getStandings() == null ? 0 : ts.getStandings();
    double points = ts.getPoints() == null ? 0d : ts.getPoints().doubleValue();
    int wins = ts.getWins() == null ? 0 : ts.getWins();

    Team t = ts.getTeam();
    String teamCode = t != null ? t.getTeamCode() : null;
    String teamName = t != null ? t.getName() : null;
    String country  = (t != null && t.getCountry() != null) ? t.getCountry().getCodeIso3() : null;

    return new TeamStandingEntryResource(
      position, points, wins, teamCode, teamName, country
    );
  }
}
