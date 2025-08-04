// src/main/java/pitmotion/env/services/imports/DriverAliasImportService.java
package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import pitmotion.env.entities.Driver;
import pitmotion.env.entities.DriverAlias;
import pitmotion.env.http.requests.imports.DriverImportRequest;
import pitmotion.env.http.requests.wrappers.DriversImportWrapper;
import pitmotion.env.repositories.DriverAliasRepository;
import pitmotion.env.repositories.DriverRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Profile("import")
public class DriverAliasImportService implements EntityImportService<DriverImportRequest, DriversImportWrapper> {

    private final RestClient restClient;
    private final DriverRepository driverRepository;
    private final DriverAliasRepository aliasRepository;
    private static final int PAGE_SIZE = 100;

    public void importAllAliases() {
        paginatedImport(
            offset -> fetch(() ->
                restClient.get()
                          .uri("/drivers?limit=" + PAGE_SIZE + "&offset=" + offset)
                          .retrieve()
                          .toEntity(DriversImportWrapper.class)
                          .getBody()
            ),
            wrapper -> wrapper != null ? wrapper.drivers() : List.of(),
            req -> processAlias(req.driverCode()),
            PAGE_SIZE
        );
    }

    private void processAlias(String rawCode) {
        Optional<Driver> optDriver = driverRepository.findByDriverCode(rawCode);
        if (optDriver.isEmpty()) {
            return;
        }
        Driver driver = optDriver.get();

        if (driver.getDriverCode().equals(rawCode)) {
            return;
        }

        boolean existAsAlias = aliasRepository.findByAlias(rawCode).isPresent();
        if (!existAsAlias) {
            DriverAlias alias = new DriverAlias();
            alias.setAlias(rawCode);
            alias.setDriver(driver);
            aliasRepository.save(alias);
        }
    }
}
