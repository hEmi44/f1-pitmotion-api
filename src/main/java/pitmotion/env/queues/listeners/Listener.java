package pitmotion.env.queues.listeners;

import pitmotion.env.queues.events.Event;

public interface Listener<T extends Event> {
  public void listen(T event);
}
