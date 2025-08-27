package pitmotion.env.http.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pitmotion.env.enums.ProfileName;
import pitmotion.env.http.resources.circuits.CircuitListResource;
import pitmotion.env.http.resources.circuits.CircuitResource;
import pitmotion.env.services.CircuitService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/circuits")
@Profile(ProfileName.HTTP)
public class CircuitsController {
  private final CircuitService circuitService;

  @GetMapping
  public ResponseEntity<?> get(@RequestParam(value = "code", required = false) String code) {
    if (code == null || code.isBlank()) {
      CircuitListResource list = circuitService.listCircuits();
      return ResponseEntity.ok(list);
    }
    CircuitResource circuit = circuitService.getCircuit(code);
    return ResponseEntity.ok(circuit);
  }
}
