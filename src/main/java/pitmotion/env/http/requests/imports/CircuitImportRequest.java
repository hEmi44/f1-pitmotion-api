package pitmotion.env.http.requests.imports;

public record CircuitImportRequest(
    String circuitCode,
    String name,
    String country,
    String city,
    Integer lenght,
    String lapRecord,
    Integer firstParticipation,
    Integer corners,
    String url
) {}
