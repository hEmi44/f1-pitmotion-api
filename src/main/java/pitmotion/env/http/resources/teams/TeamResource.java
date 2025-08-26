package pitmotion.env.http.resources.teams;

import java.util.List;

public record TeamResource(
  String code,
  String name,
  String country,
  Integer foundedYear,
  List<TeamDriverYearResource> lastDrivers
) {}
