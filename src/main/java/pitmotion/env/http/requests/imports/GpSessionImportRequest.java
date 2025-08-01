package pitmotion.env.http.requests.imports;

import pitmotion.env.enums.SessionType;

import java.time.LocalDate;
import java.time.LocalTime;

public record GpSessionImportRequest(
    SessionType type,
    LocalDate date,
    LocalTime time
) {}
