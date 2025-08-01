package pitmotion.env.http.requests.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import pitmotion.env.http.requests.imports.TeamSeasonImportRequest;

import java.util.List;

public record TeamSeasonsImportWrapper(
    @JsonProperty("championshipId") String championshipId,
    @JsonProperty("constructors_championship") List<TeamSeasonImportRequest> teams,
    @JsonProperty("limit") Integer limit,
    @JsonProperty("offset") Integer offset,
    @JsonProperty("total") Integer total
) {}
