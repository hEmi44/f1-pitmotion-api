package pitmotion.env.http.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pitmotion.env.http.resources.teams.TeamListResource;
import pitmotion.env.http.resources.teams.TeamResource;
import pitmotion.env.services.TeamService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamsController {
  private final TeamService teamService;

  @GetMapping
  public ResponseEntity<?> get(@RequestParam(value = "code", required = false) String code) {
    if (code == null || code.isBlank()) {
      TeamListResource list = teamService.listTeams();
      return ResponseEntity.ok(list);
    }
    TeamResource team = teamService.getTeam(code);
    return ResponseEntity.ok(team);
  }
}
