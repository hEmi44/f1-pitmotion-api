package pitmotion.env.http.requests.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import pitmotion.env.http.requests.imports.CircuitImportRequest;

import java.util.List;

public record CircuitsImportWrapper(
    int limit,
    int offset,
    int total,
    @JsonProperty("circuits") List<CircuitImportRequest> circuits
) {}
