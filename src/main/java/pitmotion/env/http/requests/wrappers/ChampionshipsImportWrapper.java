package pitmotion.env.http.requests.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import pitmotion.env.http.requests.imports.ChampionshipImportRequest;

import java.util.List;

public record ChampionshipsImportWrapper(
    int limit,
    int offset,
    int total,
    @JsonProperty("championships") List<ChampionshipImportRequest> championships
) {}
