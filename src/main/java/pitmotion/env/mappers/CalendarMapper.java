package pitmotion.env.mappers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.stereotype.Component;

import pitmotion.env.http.resources.calendars.CalendarGrandPrixEntryResource;
import pitmotion.env.http.resources.calendars.CalendarResource;
import pitmotion.env.http.resources.calendars.CalendarSessionResource;
import pitmotion.env.entities.GrandPrix;
import pitmotion.env.entities.GpSession;
import pitmotion.env.entities.Circuit;
import pitmotion.env.enums.SessionType;

@Component
public class CalendarMapper {

  public CalendarResource toCalendarResource(int year, List<CalendarGrandPrixEntryResource> entries) {
    return new CalendarResource(year, entries.size(), entries);
  }

  public CalendarGrandPrixEntryResource toEntryWithSessions(GrandPrix gp, List<GpSession> sessions) {
    Circuit c = gp.getCircuit();
    return new CalendarGrandPrixEntryResource(
      safeInt(gp.getRound()),
      gp.getGrandPrixCode(),
      gp.getName(),
      gp.getStartingDate(),
      c != null ? c.getCircuitCode() : null,
      c != null ? c.getName() : null,
      c != null && c.getCountry() != null ? c.getCountry().getCodeIso3() : null,
      c != null ? c.getCity() : null,
      sessions.stream().map(this::toSession).toList()
    );
  }

  public CalendarSessionResource toSession(GpSession s) {
    return new CalendarSessionResource(
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

  private int safeInt(Integer n) { return n == null ? 0 : n; }
}
