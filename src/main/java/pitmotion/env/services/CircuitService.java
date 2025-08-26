package pitmotion.env.services;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pitmotion.env.http.resources.circuits.CircuitListResource;
import pitmotion.env.http.resources.circuits.CircuitResource;
import pitmotion.env.mappers.CircuitMapper;          // <-- vérifie bien cet import
import pitmotion.env.entities.Circuit;
import pitmotion.env.repositories.CircuitRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CircuitService {
  private final CircuitRepository circuitRepository;
  private final CircuitMapper mapper;

  public CircuitListResource listCircuits() {
    List<Circuit> circuits = circuitRepository.findAll().stream()
      .sorted(Comparator.comparing(Circuit::getName,
              Comparator.nullsLast(String::compareToIgnoreCase)))
      .toList();
    return mapper.toListResource(circuits);
  }

  public CircuitResource getCircuit(String code) {
    Circuit c = circuitRepository.findByCircuitCode(code)
      .orElseThrow(() -> new EntityNotFoundException("Circuit introuvable: " + code));
    return mapper.toResource(c);
  }
}