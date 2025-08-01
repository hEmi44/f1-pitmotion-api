package pitmotion.env.mappers.imports;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pitmotion.env.entities.Championship;
import pitmotion.env.http.requests.imports.ChampionshipImportRequest;

@Component
@AllArgsConstructor
public class ChampionshipImportMapper {

    public Championship request(ChampionshipImportRequest req, Championship target) {
        target.setChampionshipCode(req.championshipCode());
        target.setName(req.name());
        target.setYear(req.year());
        target.setUrl(req.url());

        return target;
    }
}
