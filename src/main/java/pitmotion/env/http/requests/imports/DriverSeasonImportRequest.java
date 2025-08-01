package pitmotion.env.http.requests.imports;

import com.fasterxml.jackson.annotation.JsonProperty;

public record DriverSeasonImportRequest(
    @JsonProperty("driverId") String driverId,
    @JsonProperty("teamId") String teamId,
    @JsonProperty("points") Double points,
    @JsonProperty("position") Integer position,
    @JsonProperty("wins") Integer wins,
    @JsonProperty("driver") DriverDetails driver
) {
    public record DriverDetails(
        @JsonProperty("number") String number
    ) {}
}
