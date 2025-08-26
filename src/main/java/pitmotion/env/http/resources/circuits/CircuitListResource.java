package pitmotion.env.http.resources.circuits;

import java.util.List;

public record CircuitListResource(
  int count,
  List<CircuitListItemResource> entries
) {}