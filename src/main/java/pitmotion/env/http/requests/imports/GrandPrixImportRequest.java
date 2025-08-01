package pitmotion.env.http.requests.imports;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import pitmotion.env.debug.Debug;
import pitmotion.env.enums.SessionType;

public record GrandPrixImportRequest(
    @JsonProperty("raceId") String code,
    @JsonProperty("raceName") String name,
    @JsonProperty("round") Integer round,
    @JsonProperty("laps") Integer laps,
    @JsonProperty("url") String url,

    @JsonProperty("championshipId") String championshipId,
    @JsonProperty("year") Integer year,

    @JsonProperty("circuit") Circuit circuit,

    @JsonProperty("schedule")
    JsonNode schedule

) {
    public record Circuit(
        @JsonProperty("circuitId") String circuitId
    ) {}

    public List<GpSessionImportRequest> sessions() {
        List<GpSessionImportRequest> result = new ArrayList<>();
    
        if (schedule == null) return result;
    
        schedule.fieldNames().forEachRemaining(key -> {
            SessionType type = SessionType.fromApiKey(key);
            if (type == null) {
                Debug.logger().dump("❌ SessionType inconnu", key);
                return;
            }

            JsonNode sessionNode = schedule.get(key);
            if (sessionNode != null && sessionNode.hasNonNull("date") && sessionNode.hasNonNull("time")) {
                result.add(new GpSessionImportRequest(
                    type,
                    LocalDate.parse(sessionNode.get("date").asText()),
                    LocalTime.parse(sessionNode.get("time").asText().replace("Z", ""))
                ));
            }
        });
    
        return result;
    }
    
}
