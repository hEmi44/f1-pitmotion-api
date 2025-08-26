package pitmotion.env.http.resources.drivers;

public record DriverTeamYearResource(
  Integer year,
  String teamCode,
  String teamName,
  Integer number 
) {}

