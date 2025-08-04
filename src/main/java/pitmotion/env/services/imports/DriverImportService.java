package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
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
@Profile("import")
public class DriverImportService implements EntityImportService<DriverImportRequest, DriversImportWrapper> {

    private static final int PAGE_SIZE = 100;

    private final RestClient restClient;
    private final DriverRepository driverRepository;
    private final DriverAliasRepository aliasRepository;
    private final DriverImportMapper driverMapper;

    public List<Driver> importDrivers() {
        return importDriversInternal("/drivers?limit=", this::buildDriversUri);
    }

    public List<Driver> importDriversForYear(int year) {
        return importDriversInternal("/" + year + "/drivers?limit=", offset -> 
            "/" + year + "/drivers?limit=" + PAGE_SIZE + "&offset=" + offset
        );
    }

    private List<Driver> importDriversInternal(
            String basePath,
            java.util.function.Function<Integer, String> uriBuilder
    ) {
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
            req -> handleDriverReq(req, result),
            PAGE_SIZE
        );

        return result;
    }

    private String buildDriversUri(int offset) {
        return "/drivers?limit=" + PAGE_SIZE + "&offset=" + offset;
    }

    private void handleDriverReq(DriverImportRequest req, List<Driver> result) {
        String rawCode = req.driverCode();
    
        Optional<Driver> opt = driverRepository.findByDriverCode(rawCode);
    
        if (opt.isEmpty()) {
            opt = aliasRepository.findByAlias(rawCode)
                                 .map(DriverAlias::getDriver);
        }
    
        LocalDate bd = driverMapper.parseBirthday(req.birthday());
        if (opt.isEmpty() && bd != null) {
            opt = driverRepository.findByNameAndSurnameAndBirthday(
                req.name(), req.surname(), bd
            );
        }
        if (opt.isEmpty() && bd != null) {
            opt = driverRepository.findByNameAndSurnameAndBirthday(
                req.surname(), req.name(), bd
            );
        }
    
        Driver driver;
        if (opt.isPresent()) {
            driver = opt.get();
            driverMapper.request(req, driver);
            driverRepository.save(driver);
    
            boolean seenAsCode  = driver.getDriverCode().equals(rawCode);
            boolean seenAsAlias = aliasRepository.findByAlias(rawCode).isPresent();
            if (!seenAsCode && !seenAsAlias) {
                DriverAlias alias = new DriverAlias();
                alias.setAlias(rawCode);
                alias.setDriver(driver);
                aliasRepository.save(alias);
            }
    
        } else {
            driver = new Driver();
            driver.setDriverCode(rawCode);
            driverMapper.request(req, driver);
            driverRepository.save(driver);
        }
    
        result.add(driver);
    }
}
