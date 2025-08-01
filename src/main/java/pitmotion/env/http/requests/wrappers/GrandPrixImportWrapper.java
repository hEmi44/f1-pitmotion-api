package pitmotion.env.http.requests.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import pitmotion.env.http.requests.imports.GrandPrixImportRequest;

import java.util.List;

public record GrandPrixImportWrapper(
    @JsonProperty("limit") Integer limit,
    @JsonProperty("offset") Integer offset,
    @JsonProperty("total") Integer total,
    @JsonProperty("season") String season,
    @JsonProperty("races") List<GrandPrixImportRequest> races
) {}
