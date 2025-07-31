package pitmotion.env.http.requests.imports;

public record DriverImportRequest(
    String driverCode,
    String name,
    String surname,
    String shortName,
    String birthday,
    String url,
    String country
) {}
