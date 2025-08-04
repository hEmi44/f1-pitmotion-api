package pitmotion.env.http.requests.wrappers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import pitmotion.env.enums.SessionType;
import pitmotion.env.http.requests.imports.SessionResultImportRequest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SessionResultsImportWrapper(
    @JsonProperty("limit")  Integer limit,
    @JsonProperty("offset") Integer offset,
    @JsonProperty("total")  Integer total,
    @JsonProperty("season") String season,
    @JsonProperty("round")  String round,
    @JsonProperty("races")  JsonNode races
) {

    public List<SessionResultImportRequest> resultsForType(SessionType type) {
        if (type == null || races == null) {
            return Collections.emptyList();
        }
        String key = type.getJsonKey();
        JsonNode arr = races.get(key);
        if (arr == null || !arr.isArray()) {
            return Collections.emptyList();
        }
        return StreamSupport.stream(arr.spliterator(), false)
                .map(SessionResultImportRequest::fromJson)
                .filter(Objects::nonNull)
                .toList();
    }
}
