package pitmotion.env.http.requests.imports;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChampionshipImportRequest(
    @JsonProperty("championshipId") String championshipCode,
    @JsonProperty("championshipName") String name,
    @JsonProperty("year") Integer year,
    @JsonProperty("url") String url
) {}
