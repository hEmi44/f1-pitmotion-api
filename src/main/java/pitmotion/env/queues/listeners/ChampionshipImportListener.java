package pitmotion.env.queues.listeners;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import pitmotion.env.debug.Debug;
import pitmotion.env.enums.EventName;
import pitmotion.env.enums.ProfileName;
import pitmotion.env.queues.events.ChampionshipImportEvent;
import pitmotion.env.services.GlobalImportService;
import pitmotion.env.annotations.KafkaRetryableListener;

@Component
@Profile(ProfileName.QUEUE)
public class ChampionshipImportListener implements Listener<ChampionshipImportEvent> {

    private final GlobalImportService globalImportService;

    public ChampionshipImportListener(GlobalImportService globalImportService) {
        this.globalImportService = globalImportService;
    }

    @Override
    @KafkaRetryableListener(EventName.CHAMPIONSHIP_IMPORT)
    public void listen(ChampionshipImportEvent event) {
        Debug.logger().dump("Received import event for year", event.getYear());
        try {
            globalImportService.importYear(event.getYear());
            Debug.logger().dump("Import for year completed", event.getYear());
        } catch (Exception e) {
            Debug.logger().dump("Error during import for year", event.getYear(), e.getMessage());
            throw e;
        }
    }
}
