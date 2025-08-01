package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.entities.Driver;
import pitmotion.env.http.requests.imports.DriverImportRequest;
import pitmotion.env.http.requests.wrappers.DriversImportWrapper;
import pitmotion.env.mappers.imports.DriverImportMapper;
import pitmotion.env.repositories.DriverRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Profile("import")
public class DriverImportService implements EntityImportService<DriverImportRequest, DriversImportWrapper> {

    private final RestClient restClient;
    private final DriverRepository driverRepository;
    private final DriverImportMapper driverMapper;

    public List<Driver> importDrivers() {
        List<Driver> result = new ArrayList<>();
    
        paginatedImport(
            offset -> fetch(() -> restClient.get()
                .uri("/drivers?limit=100&offset=" + offset)
                .retrieve()
                .toEntity(DriversImportWrapper.class)
                .getBody()),
            wrapper -> wrapper != null ? wrapper.drivers() : List.of(),
            req -> {
                Driver entity = driverRepository.findByDriverCode(req.driverCode())
                        .orElseGet(Driver::new);
                driverMapper.request(req, entity);
                Driver saved = driverRepository.save(entity);
                result.add(saved);
            },
            100
        );
    
        return result;
    }

    public List<Driver> importDriversForYear(int year) {
        List<Driver> result = new ArrayList<>();
    
        paginatedImport(
            offset -> restClient.get()
                .uri("/" + year + "/drivers?limit=100&offset=" + offset)
                .retrieve()
                .toEntity(DriversImportWrapper.class)
                .getBody(),
            wrapper -> wrapper != null ? wrapper.drivers() : List.of(),
            req -> {
                Driver entity = driverRepository.findByDriverCode(req.driverCode())
                    .orElseGet(Driver::new);
                driverMapper.request(req, entity);
                result.add(driverRepository.save(entity));
            },
            100
        );
    
        return result;
    }
    
    
}
