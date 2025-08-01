package pitmotion.env.http.requests.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import pitmotion.env.http.requests.imports.DriverSeasonImportRequest;

import java.util.List;

public record DriverSeasonsImportWrapper(
    @JsonProperty("championshipId") String championshipId,
    @JsonProperty("drivers_championship") List<DriverSeasonImportRequest> drivers,
    @JsonProperty("limit") Integer limit,
    @JsonProperty("offset") Integer offset,
    @JsonProperty("total") Integer total
) {}
