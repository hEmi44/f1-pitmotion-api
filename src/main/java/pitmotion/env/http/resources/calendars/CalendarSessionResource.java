package pitmotion.env.http.resources.calendars;

import java.time.OffsetDateTime;
import pitmotion.env.enums.SessionType;

public record CalendarSessionResource(
  SessionType type,
  OffsetDateTime start,
  OffsetDateTime end,
  Integer laps
) {}