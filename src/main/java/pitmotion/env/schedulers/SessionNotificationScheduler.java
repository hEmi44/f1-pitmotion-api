package pitmotion.env.schedulers;

import java.time.*;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import pitmotion.env.debug.Debug;
import pitmotion.env.enums.ProfileName;
import pitmotion.env.enums.SessionType;
import pitmotion.env.queues.emitters.Emitter;
import pitmotion.env.queues.events.SessionStartNotificationEvent;
import pitmotion.env.repositories.GrandPrixRepository;
import pitmotion.env.repositories.GpSessionRepository;
import pitmotion.env.repositories.UserGpTrackerRepository;

@Component
@Profile(ProfileName.SCHEDULER) 
public class SessionNotificationScheduler {

  private static final ZoneId ZONE = ZoneId.of("Europe/Brussels");

  private final GrandPrixRepository grandPrixRepository;
  private final GpSessionRepository gpSessionRepository;
  private final UserGpTrackerRepository trackerRepository;
  private final Emitter emitter;
  private final Duration fireWindow;

  public SessionNotificationScheduler(
      GrandPrixRepository grandPrixRepository,
      GpSessionRepository gpSessionRepository,
      UserGpTrackerRepository trackerRepository,
      Emitter emitter,
      @Value("${pitmotion.notifications.window-seconds:59}") long windowSeconds
  ) {
    this.grandPrixRepository = grandPrixRepository;
    this.gpSessionRepository = gpSessionRepository;
    this.trackerRepository   = trackerRepository;
    this.emitter             = emitter;
    this.fireWindow          = Duration.ofSeconds(windowSeconds);
  }

  @Scheduled(cron = "0 * * * * *", zone = "Europe/Brussels")
  @Transactional(readOnly = true) 
  public void run() {
    Debug.logger().dump("Running session notification scheduler");
    var today = LocalDate.now(ZONE);
    var now   = Instant.now();

    var nextGp = grandPrixRepository
        .findFirstByStartingDateAfterOrderByStartingDateAsc(today)
        .orElse(null);
    if (nextGp == null) return;

    var sessions = gpSessionRepository.findByGrandPrix_IdOrderByDateAsc(nextGp.getId());
    if (sessions == null || sessions.isEmpty()) return;

    var trackers = trackerRepository.findByGrandPrix_Id(nextGp.getId());
    if (trackers == null || trackers.isEmpty()) return;

    for (var tracker : trackers) {
      var user = tracker.getUser();
      if (user == null || user.getEmail() == null || user.getEmail().isBlank()) continue;

      int minutesBefore = tracker.getNotificationOffsetMinutes() == null
          ? 10
          : Math.max(0, tracker.getNotificationOffsetMinutes());

      for (var session : sessions) {
        Instant sessionStartUtc = toInstant(session.getDate());
        Instant trigger         = sessionStartUtc.minus(Duration.ofMinutes(minutesBefore));

        if (!now.isBefore(trigger) && now.isBefore(trigger.plus(fireWindow))) {
          SessionType st = toSessionType(session.getType());

          var evt = new SessionStartNotificationEvent(
              user.getId(),
              user.getEmail(),
              nextGp.getId(),
              session.getId(),
              nextGp.getName(),
              st,
              sessionStartUtc,
              minutesBefore,
              Map.of()
          );
          emitter.send(evt);
        }
      }
    }
  }

  private Instant toInstant(Object dateField) {
    if (dateField instanceof Instant i)       return i;
    if (dateField instanceof LocalDateTime l) return l.atZone(ZONE).toInstant();
    if (dateField instanceof LocalDate d)     return d.atStartOfDay(ZONE).toInstant();
    if (dateField instanceof java.util.Date d) return d.toInstant();
    throw new IllegalStateException("Unsupported date type: " + (dateField == null ? "null" : dateField.getClass()));
  }

  private SessionType toSessionType(Object rawType) {
    if (rawType instanceof SessionType st) return st;
    if (rawType instanceof String s)       return SessionType.fromJson(s);
    try {
      var m = rawType.getClass().getMethod("getApiKey");
      Object val = m.invoke(rawType);
      if (val != null) return SessionType.fromJson(val.toString());
    } catch (Exception ignore) {}
    return SessionType.fromJson(String.valueOf(rawType));
  }
}