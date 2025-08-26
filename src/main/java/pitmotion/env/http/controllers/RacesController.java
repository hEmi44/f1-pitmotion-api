package pitmotion.env.http.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pitmotion.env.http.resources.races.RaceSessionsResource;
import pitmotion.env.services.RaceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/races")
public class RacesController {
  private final RaceService raceService;

  @GetMapping("/{raceCode}/sessions")
  public ResponseEntity<RaceSessionsResource> sessions(@PathVariable String raceCode) {
    return ResponseEntity.ok(raceService.getPlannedSessions(raceCode));
  }
}
