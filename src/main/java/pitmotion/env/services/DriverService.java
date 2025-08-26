package pitmotion.env.services;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pitmotion.env.http.resources.drivers.DriverListResource;
import pitmotion.env.http.resources.drivers.DriverResource;
import pitmotion.env.mappers.DriverMapper;
import pitmotion.env.entities.Driver;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.repositories.DriverRepository;
import pitmotion.env.repositories.DriverSeasonRepository;
import pitmotion.env.repositories.TeamSeasonRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {

  private final DriverRepository driverRepository;
  private final DriverSeasonRepository driverSeasonRepository;
  private final TeamSeasonRepository teamSeasonRepository;
  private final DriverMapper mapper;

  public DriverListResource listDrivers() {
    Integer refYear = teamSeasonRepository.findAll().stream()
        .map(ts -> ts.getChampionship() != null ? ts.getChampionship().getYear() : null)
        .filter(y -> y != null)
        .max(Integer::compareTo)
        .orElse(null);

    List<Driver> drivers = driverRepository.findAll().stream()
        .sorted(Comparator.comparing(Driver::getSurname, Comparator.nullsLast(String::compareToIgnoreCase))
                         .thenComparing(Driver::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
        .toList();

    Map<Long, List<DriverSeason>> seasonsByDriverId = driverSeasonRepository.findAll().stream()
        .collect(Collectors.groupingBy(ds -> ds.getDriver() != null ? ds.getDriver().getId() : -1L));

    return mapper.toDriverListResource(drivers, refYear, seasonsByDriverId);
  }

  public DriverResource getDriver(String code) {
    Driver driver = driverRepository.findByDriverCode(code)
        .orElseThrow(() -> new EntityNotFoundException("Driver introuvable: " + code));

    List<DriverSeason> seasons = driverSeasonRepository.findAll().stream()
        .filter(ds -> ds.getDriver() != null && ds.getDriver().getId().equals(driver.getId()))
        .sorted(Comparator.comparing(
            (DriverSeason ds) -> ds.getTeamSeason() != null && ds.getTeamSeason().getChampionship() != null
                ? ds.getTeamSeason().getChampionship().getYear() : -1
        ).reversed())
        .toList();

    return mapper.toDriverResource(driver, seasons);
  }
}
