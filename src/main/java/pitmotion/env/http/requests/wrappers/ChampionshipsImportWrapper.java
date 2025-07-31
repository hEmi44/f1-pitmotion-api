package pitmotion.env.http.requests.wrappers;

import jakarta.validation.Valid;
import pitmotion.env.http.requests.imports.ChampionshipImportRequest;

import java.util.List;

public record ChampionshipsImportWrapper(
    @Valid List<ChampionshipImportRequest> championships
) {}
