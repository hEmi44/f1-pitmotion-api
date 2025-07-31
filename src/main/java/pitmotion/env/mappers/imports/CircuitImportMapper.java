package pitmotion.env.mappers.imports;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import pitmotion.env.entities.Circuit;
import pitmotion.env.entities.Country;
import pitmotion.env.http.requests.imports.CircuitImportRequest;
import pitmotion.env.repositories.CountryRepository;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.text.Normalizer;

@Component
@AllArgsConstructor
public class CircuitImportMapper {

    private static final Logger log = LoggerFactory.getLogger(CircuitImportMapper.class);
    private final CountryRepository countryRepository;

    private static String normalize(String input) {
        String n = Normalizer.normalize(input == null ? "" : input, Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", "").toLowerCase();
    }

    public Circuit request(CircuitImportRequest req, Circuit target) {
        Country country = countryRepository.findAll().stream()
            .filter(c -> normalize(c.getNameFr()).equals(normalize(req.country()))
                      || normalize(c.getNameEn()).equals(normalize(req.country())))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException(
                "Pays introuvable (FR/EN) : " + req.country()
            ));
        target.setCountry(country);

        target.setCircuitCode(req.circuitCode());
        target.setName(req.name());
        target.setCity(req.city());
        target.setLenght(req.lenght());
        if (req.lapRecord() != null) {
            try {
                Duration d = Duration.parse(req.lapRecord());
                target.setLapRecord(d);
            } catch (DateTimeParseException e) {
                log.warn("Impossible de parser lapRecord '{}'", req.lapRecord(), e);
            }
        }
        target.setFirstParticipation(req.firstParticipation());
        target.setCorners(req.corners());
        target.setUrl(req.url());
        return target;
    }
}
