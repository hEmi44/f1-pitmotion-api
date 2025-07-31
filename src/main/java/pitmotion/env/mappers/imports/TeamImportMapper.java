package pitmotion.env.mappers.imports;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pitmotion.env.entities.Team;
import pitmotion.env.entities.Country;
import pitmotion.env.http.requests.imports.TeamImportRequest;
import pitmotion.env.repositories.CountryRepository;

import java.text.Normalizer;

@Component
@AllArgsConstructor
public class TeamImportMapper {

    private final CountryRepository countryRepository;

    private static String normalize(String input) {
        String n = Normalizer.normalize(input == null ? "" : input, Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", "").toLowerCase();
    }

    public Team request(TeamImportRequest req, Team target) {
        if (req.country() != null) {
            Country country = countryRepository.findAll().stream()
                .filter(c -> normalize(c.getNameFr()).equals(normalize(req.country()))
                          || normalize(c.getNameEn()).equals(normalize(req.country())))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                    "Pays introuvable (FR/EN) : " + req.country()
                ));
            target.setCountry(country);
        }

        target.setTeamCode(req.teamCode());
        target.setName(req.name());
        target.setFirstAppearance(req.firstAppearance());
        target.setConstructorsChampionships(req.constructorsChampionships());
        target.setDriversChampionships(req.driversChampionships());
        target.setUrl(req.url());
        return target;
    }
}
