package pitmotion.env.http.requests.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import pitmotion.env.http.requests.imports.TeamImportRequest;
import pitmotion.env.http.requests.wrappers.interfaces.BaseImportWrapper;

import java.util.List;

public record TeamsImportWrapper(
    @JsonProperty("limit") int limit,
    @JsonProperty("offset") int offset,
    @JsonProperty("total") int total,
    @JsonProperty("teams") List<TeamImportRequest> teams
) implements BaseImportWrapper<TeamImportRequest> {
    @Override public List<TeamImportRequest> getEntities() { return teams; }
}
