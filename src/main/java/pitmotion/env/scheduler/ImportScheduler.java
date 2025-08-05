package pitmotion.env.scheduler;

import java.time.LocalDate;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import pitmotion.env.debug.Debug;
import pitmotion.env.enums.ProfileName;
import pitmotion.env.queues.events.ChampionshipImportEvent;
import pitmotion.env.queues.emitters.Emitter;

@Component
@Profile(ProfileName.SCHEDULER)
public class ImportScheduler {

    private final Emitter emitter;

    public ImportScheduler(Emitter emitter) {
        this.emitter = emitter;
    }

    @Scheduled(cron = "0 0 * * * *", zone = "Europe/Brussels")
    //@Scheduled(cron = "0 * * * * *", zone = "Europe/Brussels")
    public void publishHourlyImportEvent() {
        int currentYear = LocalDate.now().getYear();
        ChampionshipImportEvent event = new ChampionshipImportEvent(currentYear);
        Debug.logger().dump(
            "Scheduler: publishing import event for year", currentYear
        );
        emitter.send(event);
    }
}
