package pitmotion.env.http.requests.wrappers.interfaces;

import java.util.List;

import pitmotion.env.http.requests.imports.interfaces.BaseImportRequest;

public interface BaseImportWrapper<T extends BaseImportRequest> {
    List<T> getEntities();
}
