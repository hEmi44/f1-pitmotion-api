// SessionResultImportRequest.java
package pitmotion.env.http.requests.imports;

import com.fasterxml.jackson.databind.JsonNode;
import pitmotion.env.debug.Debug;

public record SessionResultImportRequest(
    String driverId,
    String teamId,
    Integer position,
    Integer grid,
    Integer points,
    String time,
    String fastLap,
    String status,
    String q1,
    String q2,
    String q3
) {
    public static SessionResultImportRequest fromJson(JsonNode node) {
        try {
            String driverId = node.path("driver").path("driverId").asText(null);
            String teamId   = node.path("team").path("teamId").asText(null);

            Integer position = node.has("position") ? node.path("position").asInt() : null;
            Integer grid     = node.has("grid")     ? node.path("grid").asInt()     : null;
            Integer points   = node.has("points")   ? node.path("points").asInt()   : null;

            String time    = node.has("time")        ? node.path("time").asText(null)        : null;
            String fastLap = node.has("fastestLap")  ? node.path("fastestLap").asText(null)  : null;

            String status = node.path("status").asText(null);
            String q1     = node.path("q1").asText(null);
            String q2     = node.path("q2").asText(null);
            String q3     = node.path("q3").asText(null);

            return new SessionResultImportRequest(
                driverId, teamId, position, grid, points,
                time, fastLap, status,
                q1, q2, q3
            );
        } catch (Exception e) {
            Debug.logger().dump("SessionResultImportRequest.fromJson", node.toString(), e.getMessage());
            return null;
        }
    }
}
