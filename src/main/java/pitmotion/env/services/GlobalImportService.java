package pitmotion.env.services;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
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
@Profile("import")
public class GlobalImportService {

    private final ImportProperties importProperties;
    private final ChampionshipImportService       championshipImportService;
    private final DriverImportService             driverImportService;
    private final TeamImportService               teamImportService;
    private final CircuitImportService            circuitImportService;
    private final TeamSeasonImportService         teamSeasonImportService;
    private final DriverSeasonImportService       driverSeasonImportService;
    private final GrandPrixImportService          grandPrixImportService;
    private final GpSessionRepository             gpSessionRepository;
    private final SessionResultImportService      sessionResultImportService;

    public void importAll() {
        Debug.logger().dump("➡️ Lancement de l'import complet");

        // 1) Championnats
        Debug.logger().dump("➡️ Import des championnats");
        List<Championship> championships = championshipImportService.importChampionships();
        pause();
        Debug.logger().dump("✅ Import des championnats terminé");

        // 2) Pilotes
        Debug.logger().dump("➡️ Import des pilotes");
        driverImportService.importDrivers();
        pause();
        Debug.logger().dump("✅ Import des pilotes terminé");

        // 3) Écuries
        Debug.logger().dump("➡️ Import des écuries");
        teamImportService.importTeams();
        pause();
        Debug.logger().dump("✅ Import des écuries terminé");

        // 4) Profils écurie + pilote
        for (Championship championship : championships) {
            Debug.logger().dump("➡️ Import des profils d'équipe pour " + championship.getYear());
            teamSeasonImportService.importForChampionship(championship);
            pause();
            Debug.logger().dump("➡️ Import des profils pilotes pour " + championship.getYear());
            driverSeasonImportService.importForChampionship(championship);
            pause();
        }
        Debug.logger().dump("✅ Import des profils écurie + pilote terminé");

        // 5) Circuits
        Debug.logger().dump("➡️ Import des circuits");
        circuitImportService.importCircuits();
        pause();
        Debug.logger().dump("✅ Import des circuits terminé");

        // 6a) Grands Prix (création des sessions)
        for (Championship championship : championships) {
            Debug.logger().dump("➡️ Import des Grands Prix pour " + championship.getYear());
            grandPrixImportService.importForChampionship(championship);
            pause();
            Debug.logger().dump("✅ Import des Grands Prix terminé pour " + championship.getYear());
        }

        // 6b) Résultats de chaque session
        for (Championship championship : championships) {
            for (GrandPrix gp : grandPrixImportService.importForChampionship(championship)) {
                Debug.logger().dump("➡️ Import des résultats pour GP " + gp.getName());
                List<GpSession> sessions = gpSessionRepository.findByGrandPrix(gp);
                for (GpSession session : sessions) {
                    Debug.logger().dump("   ↪️ Session " + session.getType() + " (Round " + session.getGrandPrix().getRound() + ")");
                    sessionResultImportService.importForGpSession(session);
                }
                pause();
                Debug.logger().dump("✅ Résultats importés pour GP " + gp.getName());
            }
        }

        Debug.logger().dump("🎯 Import global terminé avec succès");
    }

    public void importYear(int year) {
        Debug.logger().dump("➡️ Lancement de l'import pour l'année " + year);

        Debug.logger().dump("➡️ Import des championnats");
        Championship championship = championshipImportService.importChampionships().stream()
            .filter(c -> c.getYear() == year)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Aucun championnat trouvé pour " + year));
        pause();
        Debug.logger().dump("✅ Championnat " + year + " prêt");

        Debug.logger().dump("➡️ Import des pilotes pour " + year);
        driverImportService.importDriversForYear(year);
        pause();
        Debug.logger().dump("✅ Pilotes " + year + " terminé");

        Debug.logger().dump("➡️ Import des écuries pour " + year);
        teamImportService.importTeamsForYear(year);
        pause();
        Debug.logger().dump("✅ Écuries " + year + " terminé");

        Debug.logger().dump("➡️ Profils d'équipe pour " + year);
        teamSeasonImportService.importForChampionship(championship);
        pause();
        Debug.logger().dump("➡️ Profils pilotes pour " + year);
        driverSeasonImportService.importForChampionship(championship);
        pause();
        Debug.logger().dump("✅ Profils saison " + year + " terminé");

        Debug.logger().dump("➡️ Import des circuits");
        circuitImportService.importCircuits();
        pause();
        Debug.logger().dump("✅ Circuits " + year + " terminé");

        Debug.logger().dump("➡️ Import des Grands Prix pour " + year);
        List<GrandPrix> gps = grandPrixImportService.importForChampionship(championship);
        pause();
        Debug.logger().dump("✅ Grands Prix " + year + " terminé");

        Debug.logger().dump("➡️ Import des résultats pour " + year);
        for (GrandPrix gp : gps) {
            Debug.logger().dump("  ↪️ GP " + gp.getName());
            List<GpSession> sessions = gpSessionRepository.findByGrandPrix(gp);
            for (GpSession session : sessions) {
                Debug.logger().dump("     • Session " + session.getType());
                sessionResultImportService.importForGpSession(session);
            }
            pause();
            Debug.logger().dump("  ✅ Résultats importés pour GP " + gp.getName());
        }

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
