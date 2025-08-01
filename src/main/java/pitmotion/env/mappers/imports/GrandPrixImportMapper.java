package pitmotion.env.mappers.imports;

import org.springframework.stereotype.Component;
import pitmotion.env.entities.GrandPrix;
import pitmotion.env.http.requests.imports.GrandPrixImportRequest;

@Component
public class GrandPrixImportMapper {

    public void request(GrandPrixImportRequest req, GrandPrix entity) {
        entity.setGrandPrixCode(req.code());
        entity.setName(req.name());
        entity.setRound(req.round());
        entity.setUrl(req.url());
    }
}
