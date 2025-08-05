package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
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
public class ChampionshipImportService
    extends EntityImportService<ChampionshipImportRequest, ChampionshipsImportWrapper> {

    private final RestClient restClient;
    private final ChampionshipRepository championshipRepository;
    private final ChampionshipImportMapper championshipMapper;

    public List<Championship> importChampionships() {
        List<Championship> result = new ArrayList<>();

        paginatedImport(
            offset -> fetch(() ->
                restClient.get()
                          .uri("/seasons?limit=" + importProperties.getPageSize()
                               + "&offset=" + offset)
                          .retrieve()
                          .toEntity(ChampionshipsImportWrapper.class)
                          .getBody()
            ),
            wrapper -> wrapper != null ? wrapper.championships() : List.of(),
            req -> processChampionship(req, result),
            importProperties.getPageSize()
        );

        return result;
    }

    private void processChampionship(ChampionshipImportRequest req,
                                     List<Championship> result) {
        Championship entity = championshipRepository
            .findByChampionshipCode(req.championshipCode())
            .orElseGet(Championship::new);

        championshipMapper.request(req, entity);
        result.add(championshipRepository.save(entity));
    }
}
