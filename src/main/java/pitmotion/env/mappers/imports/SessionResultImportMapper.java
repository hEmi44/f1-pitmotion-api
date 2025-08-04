package pitmotion.env.mappers.imports;

import org.springframework.stereotype.Component;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.GpSession;
import pitmotion.env.entities.SessionResult;
import pitmotion.env.enums.SessionType;
import pitmotion.env.http.requests.imports.SessionResultImportRequest;

import java.util.regex.Pattern;

@Component
public class SessionResultImportMapper {

    // 1) Durée « mm:ss(.SSS) » ou « hh:mm:ss(.SSS) »
    private static final Pattern DURATION      =
        Pattern.compile("^\\d{1,2}(?::\\d{2}){1,2}(?:\\.\\d+)?$");
    // 2) Écart +m:ss(.SSS)
    private static final Pattern PLUS_DURATION =
        Pattern.compile("^\\+\\d{1,2}:\\d{2}(?:\\.\\d+)?$");
    // 3) Écart +n(.xxx)?
    private static final Pattern GAP_OFFSET    =
        Pattern.compile("^\\+\\d+(?:\\.\\d+)?$");
    // 4) +n lap(s)
    private static final Pattern LAP_OFFSET    =
        Pattern.compile("^\\+\\d+ laps?$");

    public void request(
        SessionResultImportRequest req,
        SessionResult ent,
        GpSession session,
        DriverSeason ds
    ) {
        SessionType type = session.getType();

        // ────────────────────────────────────────────
        // 1) TIME vs STATUS
        // ────────────────────────────────────────────
        String rawTime = req.time();
        boolean validTime =
             rawTime != null
          && (DURATION.matcher(rawTime).matches()
           || PLUS_DURATION.matcher(rawTime).matches()
           || GAP_OFFSET.matcher(rawTime).matches()
           || LAP_OFFSET.matcher(rawTime).matches());

        if (validTime) {
            // on a un vrai “time” ou un “+…”
            ent.setTime(rawTime);
            ent.setStatus(req.status());
        } else {
            // pas de durée “standard”
            ent.setTime("/");
            // si rawTime contient par exemple “DNF” ou “Engine” on le met en status
            ent.setStatus(
                (rawTime != null && !rawTime.isBlank())
                  ? rawTime
                  : req.status()
            );
        }

        // ────────────────────────────────────────────
        // 2) Meilleurs tours (lap_time & lap_time_seconds)
        // ────────────────────────────────────────────
        switch (type) {
            case FP1, FP2, FP3 -> {
                String fl = req.fastLap();
                ent.setLapTime(fl);
                ent.setLapTimeSeconds(parseSeconds(fl));
            }
            case QUALIFYING -> {
                String best = bestOf(req.q1(), req.q2(), req.q3());
                ent.setLapTime(best);
                ent.setLapTimeSeconds(parseSeconds(best));
            }
            case SPRINT_QUALIFYING -> {
                String best = bestOf(req.sq1(), req.sq2(), req.sq3());
                ent.setLapTime(best);
                ent.setLapTimeSeconds(parseSeconds(best));
            }
            case RACE, SPRINT_RACE -> {
                String fl = req.fastLap();
                ent.setLapTime(fl);
                ent.setLapTimeSeconds(parseSeconds(fl));
            }
        }

        // ────────────────────────────────────────────
        // 3) q1/q2/q3 pour sprintQual vs qualif classique
        // ────────────────────────────────────────────
        if (type == SessionType.SPRINT_QUALIFYING) {
            ent.setQ1Time(req.sq1());
            ent.setQ2Time(req.sq2());
            ent.setQ3Time(req.sq3());
        } else {
            ent.setQ1Time(req.q1());
            ent.setQ2Time(req.q2());
            ent.setQ3Time(req.q3());
        }
        ent.setPosition(req.position());
        ent.setGridPosition(req.grid());
        ent.setGridStartingPosition(req.grid());
        ent.setPoints(req.points());
        ent.setGpSession(session);
        ent.setDriverSeason(ds);
    }

    private Integer parseSeconds(String lap) {
        if (lap == null || !lap.contains(":")) return null;
        String[] parts = lap.split(":");
        int minutes = Integer.parseInt(parts[0].trim());
        double seconds = Double.parseDouble(parts[1].trim());
        return (int) Math.round(minutes * 60 + seconds);
    }

    private String bestOf(String... laps) {
        String best     = null;
        Integer bestSec = null;
        for (String lap : laps) {
            Integer secs = parseSeconds(lap);
            if (secs == null) continue;
            if (bestSec == null || secs < bestSec) {
                bestSec = secs;
                best    = lap;
            }
        }
        return best;
    }
}
