package pitmotion.env.http.resources.calendars;

import java.util.List;

public record CalendarResource(
  int year,
  int count,
  List<CalendarGrandPrixEntryResource> entries
) {}
