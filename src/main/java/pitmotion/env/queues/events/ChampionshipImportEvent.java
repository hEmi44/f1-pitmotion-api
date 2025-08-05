package pitmotion.env.queues.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pitmotion.env.enums.EventName;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChampionshipImportEvent implements Event {
    
    public static final String EVENT_NAME = EventName.CHAMPIONSHIP_IMPORT;
    private int year;

    @Override
    public String eventName() {
        return EVENT_NAME;
    }
}