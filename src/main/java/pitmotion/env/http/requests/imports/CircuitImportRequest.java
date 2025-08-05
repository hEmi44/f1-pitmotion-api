package pitmotion.env.http.requests.imports;

import com.fasterxml.jackson.annotation.JsonProperty;

import pitmotion.env.http.requests.imports.interfaces.BaseImportRequest;

public record CircuitImportRequest(
    @JsonProperty("circuitId") String circuitCode,
    @JsonProperty("circuitName") String name,
    @JsonProperty("country") String country,
    @JsonProperty("city") String city,
    @JsonProperty("circuitLength") Integer length,
    @JsonProperty("lapRecord") String lapRecord,
    @JsonProperty("firstParticipationYear") Integer firstParticipation,
    @JsonProperty("numberOfCorners") Integer numberOfCorners,
    @JsonProperty("url") String url,

    @JsonProperty("fastestLapDriverId") String fastestLapDriverCode,
    @JsonProperty("fastestLapTeamId") String fastestLapTeamCode,
    @JsonProperty("fastestLapYear") Integer fastestLapYear
    
) implements BaseImportRequest {

    @Override public String getCode() { return circuitCode; }
    @Override public String getUrl()  { return url;  }
    @Override public String getName() { return name; }
}
