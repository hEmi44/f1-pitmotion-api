package pitmotion.env.http.resources.standings;

import java.time.OffsetDateTime;
import java.util.List;

public record DriverStandingsResource(
  int year,
  Integer afterRound,
  OffsetDateTime lastUpdated,
  int count,
  List<DriverStandingEntryResource> entries
) {}
