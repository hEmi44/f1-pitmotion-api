// pitmotion/env/queues/events/SessionStartNotificationEvent.java
package pitmotion.env.queues.events;

import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pitmotion.env.enums.EventName;
import pitmotion.env.enums.SessionType;

@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class SessionStartNotificationEvent implements Event {
  public static final String EVENT_NAME = EventName.SESSION_NOTIFY_START;

  private Long userId;
  private String email;

  private Long gpId;
  private Long gpSessionId;

  private String gpName;
  private SessionType sessionType;
  private Instant sessionStartUtc;
  private Integer minutesBefore;

  private Map<String, Object> meta;

  @Override public String eventName() { return EVENT_NAME; }
}