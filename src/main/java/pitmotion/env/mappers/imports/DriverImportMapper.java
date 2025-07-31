package pitmotion.env.mappers.imports;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pitmotion.env.entities.Driver;
import pitmotion.env.entities.Country;
import pitmotion.env.http.requests.imports.DriverImportRequest;
import pitmotion.env.repositories.CountryRepository;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@AllArgsConstructor
public class DriverImportMapper {

    private final CountryRepository countryRepository;
    private static final DateTimeFormatter BIRTHDAY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static String normalize(String input) {
        String n = Normalizer.normalize(input == null ? "" : input, Normalizer.Form.NFD);
        return n.replaceAll("\\p{M}", "").toLowerCase();
    }

    public Driver request(DriverImportRequest req, Driver target) {
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

        target.setDriverCode(req.driverCode());
        target.setName(req.name());
        target.setSurname(req.surname());
        if (req.birthday() != null) {
            try {
                target.setBirthday(LocalDate.parse(req.birthday(), BIRTHDAY_FMT));
            } catch (Exception ignored) { }
        }
        target.setShortName(req.shortName());
        target.setUrl(req.url());
        return target;
    }
}
