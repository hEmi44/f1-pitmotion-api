package pitmotion.env.http.requests.imports;

import com.fasterxml.jackson.annotation.JsonProperty;

import pitmotion.env.http.requests.imports.interfaces.BaseImportRequest;

public record TeamImportRequest(
    @JsonProperty("teamId") String teamCode,
    @JsonProperty("teamName") String name,
    @JsonProperty("teamNationality") String country,
    @JsonProperty("firstAppeareance") Integer firstAppearance,
    @JsonProperty("constructorsChampionships") Integer constructorsChampionships,
    @JsonProperty("driversChampionships") Integer driversChampionships,
    @JsonProperty("url") String url
)  implements BaseImportRequest {

    @Override public String getCode() { return teamCode; }
    @Override public String getUrl()  { return url;  }
    @Override public String getName() { return name; }
}
