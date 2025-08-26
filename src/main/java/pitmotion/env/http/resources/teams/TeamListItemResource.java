package pitmotion.env.http.resources.teams;

import java.util.List;

public record TeamListItemResource(
  String code,
  String name,
  List<TeamDriverYearResource> currentDrivers
) {}