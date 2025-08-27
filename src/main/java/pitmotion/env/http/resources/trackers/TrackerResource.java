package pitmotion.env.http.resources.trackers;

import java.time.LocalDate;
public record TrackerResource(
    Long trackerId,
    String gpCode,
    String name,
    Integer round,
    LocalDate startingDate,
    LocalDate endingDate,
    Integer offsetMinutes
) {}