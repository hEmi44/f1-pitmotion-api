package pitmotion.env.http.requests.wrappers;

import jakarta.validation.Valid;
import java.util.List;

import pitmotion.env.http.requests.imports.CircuitImportRequest;

public record CircuitsImportWrapper(
    @Valid List<CircuitImportRequest> circuits
) {}
