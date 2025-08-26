package pitmotion.env.http.resources.drivers;

public record DriverListItemResource(
  String code,
  String firstName,
  String lastName,
  Integer number,
  DriverTeamYearResource currentTeam
) {}
