package pitmotion.env.http.requests.imports;

import com.fasterxml.jackson.annotation.JsonProperty;

import pitmotion.env.http.requests.imports.interfaces.BaseImportRequest;

public record DriverImportRequest(
    @JsonProperty("driverId") String driverCode,
    @JsonProperty("name") String name,
    @JsonProperty("surname") String surname,
    @JsonProperty("shortName") String shortName,
    @JsonProperty("birthday") String birthday,
    @JsonProperty("nationality") String country,
    @JsonProperty("url") String url
) implements BaseImportRequest {

    @Override public String getCode() { return driverCode; }
    @Override public String getUrl()  { return url;  }
    @Override public String getName() { return name; }
}
