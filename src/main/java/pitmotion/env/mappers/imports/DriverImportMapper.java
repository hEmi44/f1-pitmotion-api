package pitmotion.env.mappers.imports;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pitmotion.env.entities.Driver;
import pitmotion.env.http.requests.imports.DriverImportRequest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
@AllArgsConstructor
public class DriverImportMapper {

    private final CountryResolver countryResolver;

    private static final List<DateTimeFormatter> BIRTHDAY_FORMATS = List.of(
        DateTimeFormatter.ofPattern("yyyy-MM-dd"), // 1997-09-30
        DateTimeFormatter.ofPattern("dd/MM/yyyy"), // 30/09/1997
        DateTimeFormatter.ofPattern("dd-MM-yyyy")  // 30-09-1997
    );

    public Driver request(DriverImportRequest req, Driver target) {
        if (req.country() != null) {
            target.setCountry(countryResolver.resolve(req.country()));
        }

        target.setDriverCode(req.driverCode());
        target.setName(req.name());
        target.setSurname(req.surname());

        if (req.birthday() != null) {
            for (DateTimeFormatter fmt : BIRTHDAY_FORMATS) {
                try {
                    target.setBirthday(LocalDate.parse(req.birthday(), fmt));
                    break;
                } catch (Exception ignored) {
                }
            }
        }

        target.setShortName(req.shortName());
        target.setUrl(req.url());

        return target;
    }

    public LocalDate parseBirthday(String birthday) {
        if (birthday == null) return null;
        for (DateTimeFormatter fmt : BIRTHDAY_FORMATS) {
            try {
                return LocalDate.parse(birthday, fmt);
            } catch (Exception ignored) { }
        }
        return null;
    }
}
