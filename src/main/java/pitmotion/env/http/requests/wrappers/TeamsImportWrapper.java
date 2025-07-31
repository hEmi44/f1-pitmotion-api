package pitmotion.env.http.requests.wrappers;

import jakarta.validation.Valid;
import java.util.List;

import pitmotion.env.http.requests.imports.TeamImportRequest;

public record TeamsImportWrapper(
    @Valid List<TeamImportRequest> teams
) {}
