package pitmotion.env.http.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pitmotion.env.http.resources.standings.DriverStandingsResource;
import pitmotion.env.http.resources.standings.TeamStandingsResource;
import pitmotion.env.services.StandingService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/standings")
public class StandingController {
  private final StandingService standingService;

  @GetMapping("/drivers/{year}")
  public ResponseEntity<DriverStandingsResource> driverStandings(@PathVariable int year) {
    return ResponseEntity.ok(standingService.getDriverStandings(year));
  }

  @GetMapping("/teams/{year}")
  public ResponseEntity<TeamStandingsResource> teamStandings(@PathVariable int year) {
    return ResponseEntity.ok(standingService.getTeamStandings(year));
  }
}
