package pitmotion.env.http.requests.imports;

public record ChampionshipImportRequest(
    String championshipCode,
    String name,
    Integer year,
    String url
) {}
