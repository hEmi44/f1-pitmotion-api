package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.entities.Championship;
import pitmotion.env.http.requests.imports.ChampionshipImportRequest;
import pitmotion.env.http.requests.wrappers.ChampionshipsImportWrapper;
import pitmotion.env.mappers.imports.ChampionshipImportMapper;
import pitmotion.env.repositories.ChampionshipRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Profile("import")
public class ChampionshipImportService implements EntityImportService<ChampionshipImportRequest, ChampionshipsImportWrapper> {

    private final RestClient restClient;
    private final ChampionshipRepository championshipRepository;
    private final ChampionshipImportMapper championshipMapper;

    public List<Championship> importChampionships() {
        List<Championship> result = new ArrayList<>();
    
        paginatedImport(
            offset -> fetch(() -> restClient.get()
                .uri("/seasons?limit=100&offset=" + offset)
                .retrieve()
                .toEntity(ChampionshipsImportWrapper.class)
                .getBody()),
            wrapper -> wrapper != null ? wrapper.championships() : List.of(),
            req -> {
                Championship entity = championshipRepository.findByChampionshipCode(req.championshipCode())
                        .orElseGet(Championship::new);
                championshipMapper.request(req, entity);
                Championship saved = championshipRepository.save(entity);
                result.add(saved);
            },
            100
        );
    
        return result;
    }
}
