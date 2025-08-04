package pitmotion.env.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.DriverAlias;

public interface DriverAliasRepository extends JpaRepository<DriverAlias, Long> {
    Optional<DriverAlias> findByAlias(String alias);
}
