package pitmotion.env.http.requests.trackers;

public record UpdateTrackerRequest(Long userId, Long trackerId, Integer offsetMinutes) {}
