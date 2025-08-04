package pitmotion.env.repositories;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    boolean existsByDriverCode(String driverCode);
    Optional<Driver> findByDriverCode(String driverCode);
    Optional<Driver> findByNameAndSurnameAndBirthday(String name, String surname, LocalDate birthday);
 }
