package pitmotion.env.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.Circuit;

public interface CircuitRepository extends JpaRepository<Circuit, Long> { }
