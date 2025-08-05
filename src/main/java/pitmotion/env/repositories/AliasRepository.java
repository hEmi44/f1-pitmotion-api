package pitmotion.env.repositories;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import pitmotion.env.entities.Alias;
import pitmotion.env.enums.EntityType;

public interface AliasRepository extends CrudRepository<Alias,Long> {
  Optional<Alias> findByEntityTypeAndAlias(EntityType entityType, String alias);
}
