package pitmotion.env.services.imports;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import pitmotion.env.entities.Alias;
import pitmotion.env.enums.EntityType;
import pitmotion.env.http.requests.imports.interfaces.BaseImportRequest;
import pitmotion.env.http.requests.wrappers.interfaces.BaseImportWrapper;
import pitmotion.env.repositories.AliasRepository;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AliasImportService
    extends EntityImportService<BaseImportRequest, BaseImportWrapper<BaseImportRequest>> {

    private final RestClient restClient;
    private final AliasRepository aliasRepository;

    public void importAllAliasesFor(
        EntityType entityType,
        String path,
        java.util.function.Function<BaseImportRequest, Optional<Long>> findOfficialId
    ) {
        paginatedImport(
            offset -> fetch(() -> restClient.get()
                .uri(path + "?limit=" + importProperties.getPageSize() + "&offset=" + offset)
                .retrieve()
                .toEntity((Class<BaseImportWrapper<BaseImportRequest>>)(Object) BaseImportWrapper.class)
                .getBody()
            ),
            wrapper -> wrapper != null ? wrapper.getItems() : List.of(),
            req -> {
                String code = req.getCode();
                Optional<Long> maybeId = findOfficialId.apply(req);
                if (maybeId.isEmpty()) return;
                Long officialId = maybeId.get();

                if (aliasRepository
                    .findByEntityTypeAndAlias(entityType, code)
                    .isPresent()) {
                    return;
                }

                Alias alias = new Alias();
                alias.setAlias(code);
                alias.setEntityType(entityType);
                alias.setEntityId(officialId);
                aliasRepository.save(alias);
            },
            importProperties.getPageSize()
        );
    }
}
