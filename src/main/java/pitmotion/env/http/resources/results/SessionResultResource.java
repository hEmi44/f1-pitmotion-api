package pitmotion.env.http.resources.results;

import java.util.List;

public record SessionResultResource(
  SessionResultHeaderResource header,
  int count,
  List<SessionResultEntryResource> entries
) {}