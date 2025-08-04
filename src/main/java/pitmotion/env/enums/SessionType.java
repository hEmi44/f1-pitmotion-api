package pitmotion.env.enums;

public enum SessionType {
    FP1("fp1",              "fp1Results",           "fp1"),
    FP2("fp2",              "fp2Results",           "fp2"),
    FP3("fp3",              "fp3Results",           "fp3"),
    QUALIFYING("qualy",     "qualyResults",         "qualy"),
    RACE("race",            "results",              "race"),
    SPRINT_QUALIFYING("sprintQualy", "sprintQualyResults", "sprint/qualy"),
    SPRINT_RACE("sprintRace",       "sprintRaceResults",   "sprint/race");

    private final String apiKey;   // clé venue du JSON `schedule`
    private final String jsonKey;  // propriété à lire dans `races`
    private final String path;     // fragment d’URL ([year]/[round]/<path>)

    SessionType(String apiKey, String jsonKey, String path) {
        this.apiKey  = apiKey;
        this.jsonKey = jsonKey;
        this.path    = path;
    }

    public String getApiKey()  { return apiKey; }
    public String getJsonKey() { return jsonKey; }
    public String getPath()    { return path; }

    public static SessionType fromApiKey(String key) {
        for (SessionType t : values()) {
            if (t.apiKey.equals(key)) {
                return t;
            }
        }
        return null;
    }
}
