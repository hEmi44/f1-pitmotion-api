package pitmotion.env.http.requests.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import pitmotion.env.http.requests.imports.DriverImportRequest;

import java.util.List;

public record DriversImportWrapper(
    int limit,
    int offset,
    int total,
    @JsonProperty("drivers") List<DriverImportRequest> drivers
) {}
