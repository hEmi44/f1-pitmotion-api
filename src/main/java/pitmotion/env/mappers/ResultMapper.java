package pitmotion.env.mappers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Component;
import pitmotion.env.http.resources.results.*;
import pitmotion.env.entities.*;

@Component
public class ResultMapper {

  public SessionResultResource toResult(GrandPrix gp, GpSession session, List<SessionResult> rows) {
    Circuit c = gp.getCircuit();
    SessionResultHeaderResource header = new SessionResultHeaderResource(
      gp.getChampionship() != null ? gp.getChampionship().getYear() : 0,
      gp.getRound() != null ? gp.getRound() : 0,
      gp.getGrandPrixCode(),
      session.getType(),
      toOffset(session.getDate()),
      c != null ? c.getCircuitCode() : null,
      c != null ? c.getName() : null
    );

    List<SessionResultEntryResource> entries = rows.stream()
      .map(this::toEntry)
      .toList();

    return new SessionResultResource(header, entries.size(), entries);
  }

  public SessionResultEntryResource toEntry(SessionResult r) {
    DriverSeason ds = r.getDriverSeason();
    TeamSeason ts   = ds != null ? ds.getTeamSeason() : null;

    String driverCode = ds != null && ds.getDriver() != null ? ds.getDriver().getDriverCode() : null;
    String firstName  = ds != null && ds.getDriver() != null ? ds.getDriver().getSurname() : null;
    String lastName   = ds != null && ds.getDriver() != null ? ds.getDriver().getName() : null;

    String teamCode = ts != null && ts.getTeam() != null ? ts.getTeam().getTeamCode() : null;
    String teamName = ts != null && ts.getTeam() != null ? ts.getTeam().getName() : null;

    return new SessionResultEntryResource(
      r.getPosition(),
      driverCode, firstName, lastName,
      teamCode, teamName,
      r.getGridStartingPosition(),
      null,
      r.getTime(), 
      r.getStatus(),
      r.getPoints() != null ? r.getPoints().doubleValue() : null,
      r.getQ1Time(), r.getQ2Time(), r.getQ3Time()
    );
  }

  private OffsetDateTime toOffset(java.time.LocalDate date) {
    return date != null ? date.atStartOfDay().atOffset(ZoneOffset.UTC) : null;
  }
}
