// SessionResultsImportWrapper.java
package pitmotion.env.http.requests.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import pitmotion.env.http.requests.imports.SessionResultImportRequest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public record SessionResultsImportWrapper(
    @JsonProperty("limit")  Integer limit,
    @JsonProperty("offset") Integer offset,
    @JsonProperty("total")  Integer total,
    @JsonProperty("season") String season,
    @JsonProperty("round")  String round,
    @JsonProperty("races")  JsonNode races
) {
    public List<SessionResultImportRequest> getResults() {
        if (races == null || !races.has("results")) {
            return Collections.emptyList();
        }
        var arr = races.path("results");
        if (!arr.isArray()) {
            return Collections.emptyList();
        }
        return StreamSupport.stream(arr.spliterator(), false)
            .map(SessionResultImportRequest::fromJson)
            .filter(Objects::nonNull)
            .toList();
    }
}
