package pitmotion.env.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.Circuit;

public interface CircuitRepository extends JpaRepository<Circuit, Long> { 
    boolean existsByCircuitCode(String circuitCode);
    Optional<Circuit> findByCircuitCode(String circuitCode);
}
