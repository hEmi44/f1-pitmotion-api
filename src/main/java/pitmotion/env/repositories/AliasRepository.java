package pitmotion.env.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import pitmotion.env.entities.Alias;
import pitmotion.env.enums.EntityType;

public interface AliasRepository extends JpaRepository<Alias,Long> {
  Optional<Alias> findByEntityTypeAndAlias(EntityType entityType, String alias);
}
