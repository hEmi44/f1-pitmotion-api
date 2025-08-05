package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pitmotion.env.debug.Debug;
import pitmotion.env.entities.GpSession;
import pitmotion.env.entities.GrandPrix;
import pitmotion.env.http.requests.imports.GpSessionImportRequest;
import pitmotion.env.mappers.imports.GpSessionImportMapper;
import pitmotion.env.repositories.GpSessionRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class GpSessionImportService {

    private final GpSessionRepository repo;
    private final GpSessionImportMapper mapper;

    public void importSessionsForGrandPrix(GrandPrix grandPrix, List<GpSessionImportRequest> sessions, Integer laps) {
        if (sessions == null || sessions.isEmpty()) {
            Debug.logger().dump("⚠️ Aucune session à importer", grandPrix.getGrandPrixCode());
            return;
        }

        List<GpSession> entities = sessions.stream()
            .map(req -> repo.findByGrandPrixAndType(grandPrix, req.type())
                .map(existing -> {
                    existing.setDate(req.date());
                    existing.setTime(req.time());
                    existing.setLaps(laps);
                    return existing;
                })
                .orElseGet(() -> mapper.fromRequest(req, grandPrix, laps))
            )
            .toList();

        repo.saveAll(entities);
    }

}
