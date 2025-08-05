package pitmotion.env.bootstrap;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pitmotion.env.debug.Debug;
import pitmotion.env.enums.ProfileName;
import pitmotion.env.repositories.ChampionshipRepository;
import pitmotion.env.services.GlobalImportService;

@Component
@Profile(ProfileName.IMPORT)
@AllArgsConstructor
public class ImportStartupRunner implements ApplicationRunner {

    private final GlobalImportService importService;
    private final ChampionshipRepository championshipRepository;

    @Override
    public void run(ApplicationArguments args) {
        boolean hasYear   = args.containsOption("year");
        boolean forceAll  = args.containsOption("all");
        long count        = championshipRepository.count();

        if (hasYear) {
            int year = Integer.parseInt(args.getOptionValues("year").get(0));
            Debug.logger().dump("Force import for year", year);
            importService.importYear(year);

        } else if (forceAll) {
            Debug.logger().dump("Force full import");
            importService.importAll();

        } else if (count == 0) {
            Debug.logger().dump("No data found, performing initial full import");
            importService.importAll();

        } else {
            Debug.logger().dump("Data already present (" + count
                               + " championships), skipping initial import. Waiting for events...");
        }

    }
}
