package pitmotion.env.mappers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Component;

import pitmotion.env.http.resources.races.PlannedSessionEntryResource;
import pitmotion.env.http.resources.races.RaceSessionsResource;
import pitmotion.env.entities.GrandPrix;
import pitmotion.env.entities.GpSession;
import pitmotion.env.enums.SessionType;

@Component
public class RaceMapper {

  public RaceSessionsResource toRaceSessionsResource(GrandPrix gp, List<GpSession> sessions) {
    return new RaceSessionsResource(
      gp.getGrandPrixCode(),
      gp.getName(),
      gp.getChampionship() != null ? gp.getChampionship().getYear() : 0,
      gp.getRound() != null ? gp.getRound() : 0,
      sessions.size(),
      sessions.stream().map(this::toPlanned).toList()
    );
  }

  public PlannedSessionEntryResource toPlanned(GpSession s) {
    return new PlannedSessionEntryResource(
      resolveType(s),
      toOffset(s.getDate()),
      null,
      s.getLaps()
    );
  }

  private SessionType resolveType(GpSession s) {
    if (s.getType() != null) return s.getType();
    return SessionType.fromApiKey(String.valueOf(s.getType()));
  }

  private OffsetDateTime toOffset(java.time.LocalDate date) {
    return date != null ? date.atStartOfDay().atOffset(ZoneOffset.UTC) : null;
  }
}
