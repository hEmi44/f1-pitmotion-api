package pitmotion.env.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> { }
