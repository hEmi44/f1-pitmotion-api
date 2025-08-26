package pitmotion.env.http.resources.results;

import java.time.OffsetDateTime;
import pitmotion.env.enums.SessionType;

public record SessionResultHeaderResource(
  int year,
  int round,
  String raceCode,
  SessionType session,
  OffsetDateTime date,
  String circuitCode,
  String circuitName
) {}

