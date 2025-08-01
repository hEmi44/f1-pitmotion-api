package pitmotion.env.http.requests.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import pitmotion.env.http.requests.imports.TeamImportRequest;

import java.util.List;

public record TeamsImportWrapper(
    int limit,
    int offset,
    int total,
    @JsonProperty("teams") List<TeamImportRequest> teams
) {}
