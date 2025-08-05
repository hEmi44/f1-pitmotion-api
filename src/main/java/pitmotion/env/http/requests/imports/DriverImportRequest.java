package pitmotion.env.http.requests.imports;

import com.fasterxml.jackson.annotation.JsonProperty;


public record DriverImportRequest(
    @JsonProperty("driverId") String driverCode,
    @JsonProperty("name") String name,
    @JsonProperty("surname") String surname,
    @JsonProperty("shortName") String shortName,
    @JsonProperty("birthday") String birthday,
    @JsonProperty("nationality") String country,
    @JsonProperty("url") String url
) {}
