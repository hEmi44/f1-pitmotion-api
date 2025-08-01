package pitmotion.env.mappers.imports;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pitmotion.env.debug.Debug;
import pitmotion.env.entities.Circuit;
import pitmotion.env.http.requests.imports.CircuitImportRequest;

import java.time.Duration;
import java.text.Normalizer;

@Component
@AllArgsConstructor
public class CircuitImportMapper {

    private final CountryResolver countryResolver;

    private Duration parseLapRecord(String s) {
        String[] parts = s.split(":");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Format inattendu pour lapRecord: " + s);
        }
        long minutes = Long.parseLong(parts[0]);
        long seconds = Long.parseLong(parts[1]);
        long millis = Long.parseLong(parts[2]);
        return Duration.ofMinutes(minutes)
                       .plusSeconds(seconds)
                       .plusMillis(millis);
    }

    public Circuit request(CircuitImportRequest req, Circuit target) {
        if (req.country() != null) {
            target.setCountry(countryResolver.resolve(req.country()));
        }

        target.setCircuitCode(req.circuitCode());
        target.setName(req.name());
        target.setCity(req.city());
        target.setLength(req.length());

        if (req.lapRecord() != null) {
            try {
                Duration parsed = parseLapRecord(req.lapRecord());
                target.setLapRecord(parsed);
            } catch (Exception e) {
                Debug.logger().dump("Impossible de parser lapRecord", req.lapRecord(), e.getMessage());
            }
        }

        target.setFirstParticipation(req.firstParticipation());
        target.setCorners(req.numberOfCorners());
        target.setUrl(req.url());

        return target;
    }
}
