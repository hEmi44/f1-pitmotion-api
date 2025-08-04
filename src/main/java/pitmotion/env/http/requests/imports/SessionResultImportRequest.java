// src/main/java/pitmotion/env/http/requests/imports/SessionResultImportRequest.java
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
    String q3,
    String sq1,
    String sq2,
    String sq3
) {
    public static SessionResultImportRequest fromJson(JsonNode node) {
        try {
            String driverId = node.path("driver").path("driverId").asText(null);
            String teamId   = node.path("team").path("teamId").asText(null);

            Integer position = node.has("position") ? node.path("position").asInt() : null;
            Integer grid     = node.has("grid")     ? node.path("grid").asInt()     : null;
            Integer points   = node.has("points")   ? node.path("points").asInt()   : null;

            // pour essais / sprint race on va utiliser 'time'
            String time    = node.has("time")         ? node.path("time").asText(null)        : null;
            // meilleur tour => champ 'fastestLap'
            String fastLap = node.has("fastLap")? node.path("fastLap").asText(null)  : null;

            String status = node.path("status").asText(null);

            // qualifs classiques
            String q1 = node.has("q1") ? node.path("q1").asText(null) : null;
            String q2 = node.has("q2") ? node.path("q2").asText(null) : null;
            String q3 = node.has("q3") ? node.path("q3").asText(null) : null;

            // sprint qualifying
            String sq1 = node.has("sq1") ? node.path("sq1").asText(null) : null;
            String sq2 = node.has("sq2") ? node.path("sq2").asText(null) : null;
            String sq3 = node.has("sq3") ? node.path("sq3").asText(null) : null;

            return new SessionResultImportRequest(
                driverId, teamId, position, grid, points,
                time, fastLap, status,
                q1, q2, q3,
                sq1, sq2, sq3
            );
        } catch (Exception e) {
            Debug.logger().dump("SessionResultImportRequest.fromJson", node.toString(), e.getMessage());
            return null;
        }
    }
}
