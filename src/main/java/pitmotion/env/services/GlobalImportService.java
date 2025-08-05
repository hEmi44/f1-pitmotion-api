package pitmotion.env.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pitmotion.env.configurations.ImportProperties;
import pitmotion.env.debug.Debug;
import pitmotion.env.entities.Championship;
import pitmotion.env.entities.GrandPrix;
import pitmotion.env.entities.GpSession;
import pitmotion.env.repositories.GpSessionRepository;
import pitmotion.env.services.imports.*;

import java.util.List;

@Service
@AllArgsConstructor
public class GlobalImportService {

    private final ImportProperties importProperties;
    private final ChampionshipImportService championshipImportService;
    private final DriverImportService driverImportService;
    private final TeamImportService teamImportService;
    private final CircuitImportService circuitImportService;
    private final TeamSeasonImportService teamSeasonImportService;
    private final DriverSeasonImportService driverSeasonImportService;
    private final GrandPrixImportService grandPrixImportService;
    private final GpSessionRepository gpSessionRepository;
    private final SessionResultImportService sessionResultImportService;

    public void importAll() {
        Debug.logger().dump("Starting full import");

        Debug.logger().dump("Importing championships");
        List<Championship> championships = championshipImportService.importChampionships();
        pause();

        Debug.logger().dump("Importing drivers");
        driverImportService.importDrivers();
        pause();

        Debug.logger().dump("Importing teams");
        teamImportService.importTeams();
        pause();

        for (Championship championship : championships) {
            Debug.logger().dump("Importing team seasons for " + championship.getYear());
            teamSeasonImportService.importForChampionship(championship);
            pause();

            Debug.logger().dump("Importing driver seasons for " + championship.getYear());
            driverSeasonImportService.importForChampionship(championship);
            pause();
        }

        Debug.logger().dump("Importing circuits");
        circuitImportService.importCircuits();
        pause();

        for (Championship championship : championships) {
            Debug.logger().dump("Importing Grands Prix for " + championship.getYear());
            List<GrandPrix> gps = grandPrixImportService.importForChampionship(championship);
            pause();

            for (GrandPrix gp : gps) {
                List<GpSession> sessions = gpSessionRepository.findByGrandPrix(gp);
                for (GpSession session : sessions) {
                    sessionResultImportService.importForGpSession(session);
                }
                pause();
            }
        }

        Debug.logger().dump("Full import completed");
    }

    public void importYear(int year) {
        Debug.logger().dump("Starting import for year " + year);

        Debug.logger().dump("Importing championships");
        Championship championship = championshipImportService.importChampionships().stream()
            .filter(c -> c.getYear() == year)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No championship found for " + year));
        pause();

        Debug.logger().dump("Importing drivers for " + year);
        driverImportService.importDriversForYear(year);
        pause();

        Debug.logger().dump("Importing teams for " + year);
        teamImportService.importTeamsForYear(year);
        pause();

        Debug.logger().dump("Importing team seasons for " + year);
        teamSeasonImportService.importForChampionship(championship);
        pause();

        Debug.logger().dump("Importing driver seasons for " + year);
        driverSeasonImportService.importForChampionship(championship);
        pause();

        Debug.logger().dump("Importing circuits");
        circuitImportService.importCircuits();
        pause();

        Debug.logger().dump("Importing Grands Prix for " + year);
        List<GrandPrix> gps = grandPrixImportService.importForChampionship(championship);
        pause();

        Debug.logger().dump("Importing session results for " + year);
        for (GrandPrix gp : gps) {
            List<GpSession> sessions = gpSessionRepository.findByGrandPrix(gp);
            for (GpSession session : sessions) {
                sessionResultImportService.importForGpSession(session);
            }
            pause();
        }

        Debug.logger().dump("Import for year " + year + " completed");
    }

    private void pause() {
        try {
            Thread.sleep(importProperties.getDelayMs());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
