package pitmotion.env.http.resources.calendars;

import java.time.LocalDate;
import java.util.List;

public record CalendarGrandPrixEntryResource(
  int round,
  String raceCode,
  String name,
  LocalDate date,
  String circuitCode,
  String circuitName,
  String country,
  String city,
  List<CalendarSessionResource> sessions
) {}
