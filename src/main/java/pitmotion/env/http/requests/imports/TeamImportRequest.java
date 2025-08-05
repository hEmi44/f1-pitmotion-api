package pitmotion.env.http.requests.imports;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TeamImportRequest(
    @JsonProperty("teamId") String teamCode,
    @JsonProperty("teamName") String name,
    @JsonProperty("teamNationality") String country,
    @JsonProperty("firstAppeareance") Integer firstAppearance,
    @JsonProperty("constructorsChampionships") Integer constructorsChampionships,
    @JsonProperty("driversChampionships") Integer driversChampionships,
    @JsonProperty("url") String url
)  {}
