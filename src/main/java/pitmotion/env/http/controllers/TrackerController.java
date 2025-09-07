package pitmotion.env.http.controllers;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pitmotion.env.enums.ProfileName;
import pitmotion.env.http.requests.trackers.CreateTrackerRequest;
import pitmotion.env.http.requests.trackers.DeleteTrackerRequest;
import pitmotion.env.http.requests.trackers.UpdateTrackerRequest;
import pitmotion.env.http.resources.trackers.TrackerResource;
import pitmotion.env.services.TrackerService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/trackers")
@RequiredArgsConstructor
@Profile(ProfileName.HTTP)
public class TrackerController {

    private final TrackerService service;

    @GetMapping
    public List<TrackerResource> list(@RequestParam("userId") Long userId) {
        return service.listFuture(userId);
    }


    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateTrackerRequest req) {
        Long trackerId = service.create(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("trackerId", trackerId));
    }


    @PutMapping("/{trackerId}")
    public ResponseEntity<?> update(@PathVariable Long trackerId,
                                    @RequestBody UpdateTrackerRequest req) {
        if (!trackerId.equals(req.trackerId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "trackerId du path != trackerId du body"));
        }
        service.update(req);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{trackerId}")
    public ResponseEntity<?> delete(@PathVariable Long trackerId,
                                    @RequestBody DeleteTrackerRequest req) {
        if (!trackerId.equals(req.trackerId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "trackerId du path != trackerId du body"));
        }
        service.delete(req);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> notFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<?> business(IllegalStateException ex) {
        return ResponseEntity.unprocessableEntity().body(Map.of("error", ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
