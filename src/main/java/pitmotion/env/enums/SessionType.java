package pitmotion.env.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SessionType {
    FP1("fp1", "fp1Results", "fp1"),
    FP2("fp2", "fp2Results", "fp2"),
    FP3("fp3", "fp3Results", "fp3"),
    QUALIFYING("qualy", "qualyResults", "qualy"),
    RACE("race", "results", "race"),
    SPRINT_QUALIFYING("sprintQualy", "sprintQualyResults", "sprint/qualy"),
    SPRINT_RACE("sprintRace", "sprintRaceResults", "sprint/race");

    private final String apiKey;
    private final String jsonKey;
    private final String path;

    SessionType(String apiKey, String jsonKey, String path) {
        this.apiKey  = apiKey;
        this.jsonKey = jsonKey;
        this.path    = path;
    }

    public String getApiKey()  { return apiKey; }
    public String getJsonKey() { return jsonKey; }
    public String getPath()    { return path; }

    @JsonValue 
    public String json() { return apiKey; }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SessionType fromJson(String value) {
        if (value == null) return null;
        String v = value.trim();
        for (SessionType t : values()) {
            if (t.apiKey.equalsIgnoreCase(v) || t.path.equalsIgnoreCase(v) || t.name().equalsIgnoreCase(v)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Unknown session type: " + value);
    }

    public static SessionType fromApiKey(String key) {
        for (SessionType t : values()) {
            if (t.apiKey.equals(key)) return t;
        }
        return null;
    }
}
