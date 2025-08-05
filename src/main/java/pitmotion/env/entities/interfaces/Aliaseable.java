package pitmotion.env.entities.interfaces;

import pitmotion.env.enums.EntityType;

public interface Aliaseable {
    EntityType getEntityType();
    Long getId();
    String getCode();
}
