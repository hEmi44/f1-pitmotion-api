package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.entities.Driver;
import pitmotion.env.enums.EntityType;
import pitmotion.env.http.requests.imports.DriverImportRequest;
import pitmotion.env.http.requests.wrappers.DriversImportWrapper;
import pitmotion.env.mappers.imports.DriverImportMapper;
import pitmotion.env.repositories.AliasRepository;
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
    private final AliasRepository aliasRepository;
    private final DriverImportMapper driverMapper;
    private final AliasImportService aliasImportService;

    public List<Driver> importDrivers() {
        List<Driver> result = new ArrayList<>();
        List<Pair<String, Long>> newAliases = new ArrayList<>();

        paginatedImport(
            offset -> fetch(() ->
                restClient.get()
                          .uri("/drivers?limit=" + importProperties.getPageSize() + "&offset=" + offset)
                          .retrieve()
                          .toEntity(DriversImportWrapper.class)
                          .getBody()
            ),
            wrapper -> wrapper != null ? wrapper.drivers() : List.of(),
            req -> processDriver(req, result, newAliases),
            importProperties.getPageSize()
        );

        aliasImportService.saveNewAliases(EntityType.DRIVER, newAliases);
        return result;
    }

    public List<Driver> importDriversForYear(int year) {
        List<Driver> result = new ArrayList<>();
        List<Pair<String, Long>> newAliases = new ArrayList<>();

        paginatedImport(
            offset -> fetch(() ->
                restClient.get()
                          .uri(String.format("/%d/drivers?limit=%d&offset=%d",
                              year, importProperties.getPageSize(), offset))
                          .retrieve()
                          .toEntity(DriversImportWrapper.class)
                          .getBody()
            ),
            wrapper -> wrapper != null ? wrapper.drivers() : List.of(),
            req -> processDriver(req, result, newAliases),
            importProperties.getPageSize()
        );

        aliasImportService.saveNewAliases(EntityType.DRIVER, newAliases);
        return result;
    }

    private void processDriver(DriverImportRequest req, List<Driver> result, List<Pair<String, Long>> newAliases) {
        String incomingCode = req.driverCode();

        Optional<Driver> byCode = driverRepository.findByDriverCode(incomingCode);
        Optional<Driver> byAlias = byCode.isEmpty()
            ? aliasRepository.findByEntityTypeAndAlias(EntityType.DRIVER, incomingCode)
                             .flatMap(a -> driverRepository.findById(a.getEntityId()))
            : Optional.empty();

        Optional<Driver> existingOpt = byCode.or(() -> byAlias);
        if (existingOpt.isEmpty()) {
            LocalDate bd = driverMapper.parseBirthday(req.birthday());
            if (bd != null) {
                existingOpt = driverRepository.findByNameAndSurnameAndBirthday(req.name(), req.surname(), bd);
                if (existingOpt.isEmpty()) {
                    existingOpt = driverRepository.findByNameAndSurnameAndBirthday(req.surname(), req.name(), bd);
                }
            }
        }

        Driver entity;
        String oldCode = null;
        if (existingOpt.isPresent()) {
            entity = existingOpt.get();
            oldCode = entity.getDriverCode();
        } else {
            entity = new Driver();
        }

        driverMapper.request(req, entity);
        if (oldCode != null) {
            entity.setDriverCode(oldCode);
        }

        Driver saved = driverRepository.save(entity);
        result.add(saved);

        if (oldCode != null && !oldCode.equals(incomingCode)) {
            newAliases.add(Pair.of(incomingCode, saved.getId()));
        }
    }
}
