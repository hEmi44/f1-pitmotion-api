package pitmotion.env.mappers;

import org.springframework.stereotype.Component;
import pitmotion.env.http.resources.circuits.CircuitListItemResource;
import pitmotion.env.http.resources.circuits.CircuitListResource;
import pitmotion.env.http.resources.circuits.CircuitResource;
import pitmotion.env.entities.Circuit;

import java.util.List;

@Component
public class CircuitMapper {

  public CircuitListResource toListResource(List<Circuit> circuits) {
    List<CircuitListItemResource> items = circuits.stream()
        .map(this::toListItem)
        .toList();
    return new CircuitListResource(items.size(), items);
  }

  public CircuitListItemResource toListItem(Circuit c) {
    return new CircuitListItemResource(
      c.getCircuitCode(),
      c.getName(),
      c.getCountry() != null ? c.getCountry().getCodeIso3() : null
    );
  }

  public CircuitResource toResource(Circuit c) {
    return new CircuitResource(
      c.getCircuitCode(),
      c.getName(),
      c.getCountry() != null ? c.getCountry().getCodeIso3() : null,
      c.getCity(),
      c.getLength()
    );
  }
}
