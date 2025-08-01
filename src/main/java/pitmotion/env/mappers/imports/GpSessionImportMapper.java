package pitmotion.env.mappers.imports;

import org.springframework.stereotype.Component;
import pitmotion.env.entities.GpSession;
import pitmotion.env.entities.GrandPrix;
import pitmotion.env.enums.SessionType;
import pitmotion.env.http.requests.imports.GpSessionImportRequest;

@Component
public class GpSessionImportMapper {
    public GpSession fromRequest(GpSessionImportRequest req, GrandPrix grandPrix, int laps) {
        GpSession session = new GpSession();
        session.setType(req.type());
        session.setDate(req.date());
        session.setTime(req.time());

        if (req.type() == SessionType.RACE) {
            session.setLaps(laps);
        }

        session.setGrandPrix(grandPrix);
        return session;
    }
}
