package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pitmotion.env.entities.GrandPrix;
import pitmotion.env.entities.GpSession;
import pitmotion.env.http.requests.imports.GpSessionImportRequest;
import pitmotion.env.mappers.imports.GpSessionImportMapper;
import pitmotion.env.repositories.GpSessionRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class GpSessionImportService {

    private final GpSessionRepository repo;
    private final GpSessionImportMapper mapper;

    public void importSessionsForGrandPrix(GrandPrix gp, List<GpSessionImportRequest> sessions, Integer laps) {
        if (sessions == null || sessions.isEmpty()) return;
        sessions.stream()
            .map(req -> repo.findByGrandPrixAndType(gp, req.type())
                .map(existing -> updateSession(existing, req, laps))
                .orElseGet(() -> mapper.fromRequest(req, gp, laps))
            )
            .toList()
            .forEach(repo::save);
    }

    private GpSession updateSession(GpSession existing, GpSessionImportRequest req, Integer laps) {
        existing.setDate(req.date());
        existing.setTime(req.time());
        existing.setLaps(laps);
        return existing;
    }
}
