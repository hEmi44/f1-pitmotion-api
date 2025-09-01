package pitmotion.env.services.notifications;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import pitmotion.env.queues.events.SessionStartNotificationEvent;

@Component
public class NotificationMessageBuilder {
  private static final ZoneId ZONE = ZoneId.of("Europe/Brussels");
  private static final DateTimeFormatter FMT =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZONE);

  public String subject(SessionStartNotificationEvent e) {
    return "PitMotion — " + e.getSessionType().getApiKey().toUpperCase()
        + " " + e.getGpName() + " dans " + e.getMinutesBefore() + " min";
  }

  public String body(SessionStartNotificationEvent e, String username) {
    return """
        Bonjour %s,

        Rappel : la session %s du GP %s démarre à %s (dans %d minutes).

        — PitMotion
        """.formatted(
        username == null ? "" : username,
        e.getSessionType().getApiKey().toUpperCase(),
        e.getGpName(),
        FMT.format(e.getSessionStartUtc()),
        e.getMinutesBefore()
    );
  }
}