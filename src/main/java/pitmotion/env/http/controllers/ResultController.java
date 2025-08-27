package pitmotion.env.http.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pitmotion.env.http.resources.results.SessionResultResource;
import pitmotion.env.services.ResultService;
import pitmotion.env.enums.ProfileName;
import pitmotion.env.enums.SessionType;

@RestController
@RequiredArgsConstructor
@RequestMapping("/results")
@Profile(ProfileName.HTTP)
public class ResultController {
  private final ResultService resultService;

  @GetMapping("/{year}/{round}/{session}")
  public ResponseEntity<SessionResultResource> getSessionResults(@PathVariable int year,
                                                                 @PathVariable int round,
                                                                 @PathVariable("session") String session) {
    SessionType sessionType = SessionType.valueOf(session.toUpperCase());
    return ResponseEntity.ok(resultService.getSessionResults(year, round, sessionType));
  }
}
