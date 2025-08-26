package pitmotion.env.http.resources.teams;

public record TeamDriverYearResource(
  Integer year,
  String driverCode,
  String firstName,
  String lastName,
  Integer number
) {}
