package pitmotion.env.http.requests.imports;

public record TeamImportRequest(
    String teamCode,
    String name,
    String country,
    Integer firstAppearance,
    Integer constructorsChampionships,
    Integer driversChampionships,
    String url
) {}
