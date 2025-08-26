package pitmotion.env.http.resources.standings;

public record DriverStandingEntryResource(
  int position,
  double points,
  int wins,
  String driverCode,
  String driverFirstName,
  String driverLastName,
  String teamCode,
  String teamName
) {}