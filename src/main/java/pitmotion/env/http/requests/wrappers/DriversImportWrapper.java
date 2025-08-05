package pitmotion.env.http.requests.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import pitmotion.env.http.requests.imports.DriverImportRequest;
import pitmotion.env.http.requests.wrappers.interfaces.BaseImportWrapper;

import java.util.List;

public record DriversImportWrapper(
    @JsonProperty("limit") Integer limit,
    @JsonProperty("offset") Integer offset,
    @JsonProperty("total") Integer total,
    @JsonProperty("drivers") List<DriverImportRequest> drivers
) implements BaseImportWrapper<DriverImportRequest> {
    @Override public List<DriverImportRequest> getEntities() { return drivers; }
}
