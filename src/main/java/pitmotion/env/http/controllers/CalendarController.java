package pitmotion.env.http.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pitmotion.env.http.resources.calendars.CalendarResource;
import pitmotion.env.services.CalendarService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {
  private final CalendarService calendarService;

  @GetMapping("/{year}")
  public ResponseEntity<CalendarResource> getSeason(@PathVariable int year) {
    return ResponseEntity.ok(calendarService.getSeasonCalendar(year));
  }
}
