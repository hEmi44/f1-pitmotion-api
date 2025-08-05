package pitmotion.env.http.requests.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import pitmotion.env.http.requests.imports.CircuitImportRequest;
import pitmotion.env.http.requests.wrappers.interfaces.BaseImportWrapper;

import java.util.List;

public record CircuitsImportWrapper(
    @JsonProperty("limit") Integer limit,
    @JsonProperty("offset") Integer offset,
    @JsonProperty("total") Integer total,
    @JsonProperty("circuits") List<CircuitImportRequest> circuits
) implements BaseImportWrapper<CircuitImportRequest> {
    @Override public List<CircuitImportRequest> getEntities() { return circuits; }
}
