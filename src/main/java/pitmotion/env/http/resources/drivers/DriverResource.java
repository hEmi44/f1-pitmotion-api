package pitmotion.env.http.resources.drivers;

import java.time.LocalDate;
import java.util.List;

public record DriverResource(
  String code,
  String firstName,
  String lastName,
  Integer number,
  String nationality,
  LocalDate birthDate,
  List<DriverTeamYearResource> lastTeams
) {}
