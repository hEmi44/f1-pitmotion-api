package pitmotion.env.http.resources.teams;

import java.util.List;

public record TeamListResource(
  Integer referenceYear,
  int count,
  List<TeamListItemResource> entries
) {}
