package pitmotion.env.http.resources.races;

import java.util.List;

public record RaceSessionsResource(
  String raceCode,
  String raceName,
  int year,
  int round,
  int count,
  List<PlannedSessionEntryResource> entries
) {}