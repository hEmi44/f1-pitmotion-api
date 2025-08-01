package pitmotion.env.http.requests.imports;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TeamSeasonImportRequest(
    @JsonProperty("teamId") String teamId,
    @JsonProperty("points") Double points,
    @JsonProperty("position") Integer position,
    @JsonProperty("wins") Integer wins
) {}
