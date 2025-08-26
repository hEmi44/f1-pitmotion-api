package pitmotion.env.http.resources.standings;

public record TeamStandingEntryResource(
  int position,
  double points,
  int wins,
  String teamCode,
  String teamName,
  String country
) {}
