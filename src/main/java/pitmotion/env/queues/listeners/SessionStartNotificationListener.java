package pitmotion.env.queues.listeners;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import pitmotion.env.annotations.KafkaRetryableListener;
import pitmotion.env.debug.Debug;
import pitmotion.env.enums.EventName;
import pitmotion.env.enums.ProfileName;
import pitmotion.env.queues.events.SessionStartNotificationEvent;
import pitmotion.env.services.notifications.EmailNotificationService;
import pitmotion.env.services.notifications.NotificationMessageBuilder;

@Component
@Profile(ProfileName.QUEUE)
public class SessionStartNotificationListener implements Listener<SessionStartNotificationEvent> {

  private final EmailNotificationService mail;
  private final NotificationMessageBuilder builder;

  public SessionStartNotificationListener(EmailNotificationService mail, NotificationMessageBuilder builder) {
    this.mail = mail;
    this.builder = builder;
  }

  @Override
  @KafkaRetryableListener(EventName.SESSION_NOTIFY_START)
  public void listen(SessionStartNotificationEvent e) {
    var subject = builder.subject(e);
    var body    = builder.body(e, null); 
    Debug.logger().dump("Send mail", e.getEmail(), subject);
    mail.send(e.getEmail(), subject, body);
  }
}