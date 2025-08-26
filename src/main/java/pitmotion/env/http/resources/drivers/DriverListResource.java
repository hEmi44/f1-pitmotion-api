package pitmotion.env.http.resources.drivers;

import java.util.List;

public record DriverListResource(
  Integer referenceYear,
  int count,
  List<DriverListItemResource> entries
) {}
