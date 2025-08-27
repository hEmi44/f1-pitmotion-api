package pitmotion.env.http.requests.trackers;

public record CreateTrackerRequest(Long userId, String gpCode, Integer offsetMinutes) {}


