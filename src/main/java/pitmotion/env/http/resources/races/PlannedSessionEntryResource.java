package pitmotion.env.http.resources.races;

import java.time.OffsetDateTime;
import pitmotion.env.enums.SessionType;

public record PlannedSessionEntryResource(
  SessionType type,
  OffsetDateTime start,
  OffsetDateTime end,
  Integer laps
) {}