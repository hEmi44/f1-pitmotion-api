package pitmotion.env.services;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import pitmotion.env.configurations.ImportProperties;
import pitmotion.env.debug.Debug;
import pitmotion.env.entities.Championship;
import pitmotion.env.services.imports.*;

import java.util.List;

@Service
@AllArgsConstructor
@Profile("import")
public class GlobalImportService {

    private final ImportProperties importProperties;
    private final ChampionshipImportService championshipImportService;
    private final DriverImportService driverImportService;
    private final TeamImportService teamImportService;
    private final CircuitImportService circuitImportService;
    private final TeamSeasonImportService teamSeasonImportService;
    private final DriverSeasonImportService driverSeasonImportService;
    private final GrandPrixImportService grandPrixImportService;

    public void importAll() {
        Debug.logger().dump("➡️ Lancement de l'import complet");

        List<Championship> championships = championshipImportService.importChampionships();
        pause();
        Debug.logger().dump("✅ Import des championnats terminé");

        driverImportService.importDrivers();
        pause();
        Debug.logger().dump("✅ Import des pilotes terminé");

        teamImportService.importTeams();
        pause();
        Debug.logger().dump("✅ Import des écuries terminé");

        for (Championship championship : championships) {
            Debug.logger().dump("➡️ Import des profils d'équipe pour " + championship.getYear());
            teamSeasonImportService.importForChampionship(championship);
            pause();

            Debug.logger().dump("➡️ Import des profils pilotes pour " + championship.getYear());
            driverSeasonImportService.importForChampionship(championship);
            pause();
        }
        Debug.logger().dump("✅ Import des profils écurie + pilote terminé");

        circuitImportService.importCircuits();
        pause();
        Debug.logger().dump("✅ Import des circuits terminé");

        for (Championship championship : championships) {
            Debug.logger().dump("➡️ Import des Grands Prix pour " + championship.getYear());
            grandPrixImportService.importForChampionship(championship);
            pause();
        }

        Debug.logger().dump("🎯 Import global terminé avec succès");
    }

    public void importYear(int year) {
        Debug.logger().dump("➡️ Lancement de l'import pour l'année " + year);

        Championship championship = championshipImportService.importChampionships().stream()
            .filter(c -> c.getYear() == year)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Aucun championnat trouvé pour l'année " + year));

        driverImportService.importDriversForYear(year);
        pause();
        Debug.logger().dump("✅ Pilotes " + year + " terminé");

        teamImportService.importTeamsForYear(year);
        pause();
        Debug.logger().dump("✅ Écuries " + year + " terminé");

        Debug.logger().dump("➡️ Profils d'équipe pour " + year);
        teamSeasonImportService.importForChampionship(championship);
        pause();

        Debug.logger().dump("➡️ Profils pilotes pour " + year);
        driverSeasonImportService.importForChampionship(championship);
        pause();

        circuitImportService.importCircuits();
        pause();
        Debug.logger().dump("✅ Circuits terminés (import global)");

        Debug.logger().dump("➡️ Import des Grands Prix pour " + year);
        grandPrixImportService.importForChampionship(championship);
        pause();

        Debug.logger().dump("🎯 Import terminé pour l'année " + year);
    }

    private void pause() {
        try {
            Thread.sleep(importProperties.getDelayMs());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
