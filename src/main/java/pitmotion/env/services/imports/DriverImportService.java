package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.entities.Driver;
import pitmotion.env.entities.DriverAlias;
import pitmotion.env.http.requests.imports.DriverImportRequest;
import pitmotion.env.http.requests.wrappers.DriversImportWrapper;
import pitmotion.env.mappers.imports.DriverImportMapper;
import pitmotion.env.repositories.DriverAliasRepository;
import pitmotion.env.repositories.DriverRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DriverImportService extends EntityImportService<DriverImportRequest, DriversImportWrapper> {

    private final RestClient restClient;
    private final DriverRepository driverRepository;
    private final DriverAliasRepository aliasRepository;
    private final DriverImportMapper driverMapper;

    public List<Driver> importDrivers() {
        return importDriversInternal(offset ->
            "/drivers?limit=" + importProperties.getPageSize() + "&offset=" + offset
        );
    }

    public List<Driver> importDriversForYear(int year) {
        return importDriversInternal(offset ->
            "/" + year + "/drivers?limit=" + importProperties.getPageSize() + "&offset=" + offset
        );
    }

    private List<Driver> importDriversInternal(java.util.function.Function<Integer, String> uriBuilder) {
        List<Driver> result = new ArrayList<>();

        paginatedImport(
            offset -> fetch(() ->
                restClient.get()
                          .uri(uriBuilder.apply(offset))
                          .retrieve()
                          .toEntity(DriversImportWrapper.class)
                          .getBody()
            ),
            wrapper -> wrapper != null ? wrapper.drivers() : List.of(),
            req -> {
                String rawCode = req.driverCode();
                Optional<Driver> opt = driverRepository.findByDriverCode(rawCode)
                    .or(() -> aliasRepository.findByAlias(rawCode).map(DriverAlias::getDriver));

                LocalDate bd = driverMapper.parseBirthday(req.birthday());
                if (opt.isEmpty() && bd != null) {
                    opt = driverRepository.findByNameAndSurnameAndBirthday(req.name(), req.surname(), bd);
                }
                if (opt.isEmpty() && bd != null) {
                    opt = driverRepository.findByNameAndSurnameAndBirthday(req.surname(), req.name(), bd);
                }

                Driver driver = opt.map(existing -> {
                    String canonical = existing.getDriverCode();
                    driverMapper.request(req, existing);
                    existing.setDriverCode(canonical);
                    driverRepository.save(existing);
                    if (!canonical.equals(rawCode) && aliasRepository.findByAlias(rawCode).isEmpty()) {
                        DriverAlias a = new DriverAlias();
                        a.setAlias(rawCode);
                        a.setDriver(existing);
                        aliasRepository.save(a);
                    }
                    return existing;
                }).orElseGet(() -> {
                    Driver d = new Driver();
                    d.setDriverCode(rawCode);
                    driverMapper.request(req, d);
                    return driverRepository.save(d);
                });

                result.add(driver);
            },
            importProperties.getPageSize()
        );

        return result;
    }
}
