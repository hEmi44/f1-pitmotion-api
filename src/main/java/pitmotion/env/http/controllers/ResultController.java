package pitmotion.env.http.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pitmotion.env.http.resources.results.SessionResultResource;
import pitmotion.env.services.ResultService;
import pitmotion.env.enums.SessionType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/results")
public class ResultController {
  private final ResultService resultService;

  @GetMapping("/{year}/{round}/{session}")
  public ResponseEntity<SessionResultResource> getSessionResults(@PathVariable int year,
                                                                 @PathVariable int round,
                                                                 @PathVariable("session") SessionType sessionType) {
    return ResponseEntity.ok(resultService.getSessionResults(year, round, sessionType));
  }
}
