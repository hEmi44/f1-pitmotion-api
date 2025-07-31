package pitmotion.env.http.requests.wrappers;

import jakarta.validation.Valid;
import java.util.List;

import pitmotion.env.http.requests.imports.DriverImportRequest;

public record DriversImportWrapper(
    @Valid List<DriverImportRequest> drivers
) {}
