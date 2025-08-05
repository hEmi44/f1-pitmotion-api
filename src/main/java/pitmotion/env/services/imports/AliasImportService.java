package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import pitmotion.env.entities.Alias;
import pitmotion.env.enums.EntityType;
import pitmotion.env.repositories.AliasRepository;

import java.util.Collection;


@Service
@AllArgsConstructor
public class AliasImportService {

  private final AliasRepository aliasRepository;


  public void saveNewAliases(EntityType entityType,
                             Collection<Pair<String, Long>> codesAndIds) {
      for (var pair : codesAndIds) {
          String code = pair.getLeft();
          Long   id   = pair.getRight();
          if (aliasRepository
                .findByEntityTypeAndAlias(entityType, code)
                .isEmpty()) {
              Alias a = new Alias();
              a.setEntityType(entityType);
              a.setAlias(code);
              a.setEntityId(id);
              aliasRepository.save(a);
          }
      }
  }
}
