package pitmotion.env.enums;

import java.util.Map;

public enum SessionType {
    FP1("fp1"),
    FP2("fp2"),
    FP3("fp3"),
    QUALIFYING("qualy"),
    SPRINT_QUALIFYING("sprintQualy"),
    SPRINT_RACE("sprintRace"),
    RACE("race");

    private final String apiKey;

    SessionType(String apiKey) {
        this.apiKey = apiKey;
    }

    public String apiKey() {
        return apiKey;
    }

    private static final Map<String, SessionType> BY_API_KEY = Map.ofEntries(
        Map.entry("fp1", FP1),
        Map.entry("fp2", FP2),
        Map.entry("fp3", FP3),
        Map.entry("qualy", QUALIFYING),
        Map.entry("sprintQualy", SPRINT_QUALIFYING),
        Map.entry("sprintRace", SPRINT_RACE),
        Map.entry("race", RACE)
    );

    public static SessionType fromApiKey(String key) {
        return BY_API_KEY.get(key);
    }
}
