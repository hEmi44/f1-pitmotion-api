package pitmotion.env.bootstrap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import pitmotion.env.services.GlobalImportService;

@Component
@Profile("import")
public class ImportStartupRunner implements ApplicationRunner {

    private final GlobalImportService importService;

    public ImportStartupRunner(GlobalImportService importService) {
        this.importService = importService;
    }

    @Override
    public void run(ApplicationArguments args) {
        //importService.importAll();
        importService.importYear(2022);
    }
}
