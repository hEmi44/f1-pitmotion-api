package pitmotion.env.mappers.imports;

import org.springframework.stereotype.Component;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.GpSession;
import pitmotion.env.entities.SessionResult;
import pitmotion.env.http.requests.imports.SessionResultImportRequest;

@Component
public class SessionResultImportMapper {

    public void request(
        SessionResultImportRequest req,
        SessionResult ent,
        GpSession session,
        DriverSeason ds
    ) {
        ent.setTime(req.time());
        ent.setLapTime(req.fastLap());
        ent.setLapTimeSeconds(parseSeconds(req.fastLap()));
        ent.setPosition(req.position());
        ent.setQ1Time(req.q1());
        ent.setQ2Time(req.q2());
        ent.setQ3Time(req.q3());
        ent.setGridPosition(req.grid());
        ent.setGridStartingPosition(req.grid());
        ent.setPoints(req.points());
        ent.setStatus(req.status());
        ent.setGpSession(session);
        ent.setDriverSeason(ds);
    }

    private Integer parseSeconds(String lap) {
        if (lap == null || !lap.contains(":")) {
            return null;
        }
        String[] parts = lap.split(":");
        int m = Integer.parseInt(parts[0]);
        double s = Double.parseDouble(parts[1]);
        return (int) (m * 60 + s);
    }
}
