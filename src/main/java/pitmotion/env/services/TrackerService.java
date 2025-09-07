package pitmotion.env.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pitmotion.env.entities.GrandPrix;
import pitmotion.env.entities.User;
import pitmotion.env.entities.UserGpTracker;
import pitmotion.env.entities.UserGpTrackerId;
import pitmotion.env.http.requests.trackers.CreateTrackerRequest;
import pitmotion.env.http.requests.trackers.DeleteTrackerRequest;
import pitmotion.env.http.requests.trackers.UpdateTrackerRequest;
import pitmotion.env.http.resources.trackers.TrackerResource;
import pitmotion.env.repositories.GrandPrixRepository;
import pitmotion.env.repositories.UserGpTrackerRepository;
import pitmotion.env.repositories.UserRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TrackerService {

  private final UserRepository userRepo;
  private final GrandPrixRepository gpRepo;
  private final UserGpTrackerRepository trackerRepo;

  public Long create(CreateTrackerRequest req) {
    User user = userRepo.findById(req.userId())
        .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
    GrandPrix gp = gpRepo.findByGrandPrixCode(req.gpCode())
        .orElseThrow(() -> new EntityNotFoundException("GP introuvable"));

    if (gp.getEndingDate() != null && gp.getEndingDate().isBefore(LocalDate.now())) {
      throw new IllegalStateException("Impossible de tracker un GP passé");
    }

    UserGpTrackerId id = new UserGpTrackerId(user.getId(), gp.getId());
    if (trackerRepo.existsById(id)) {
      if (req.offsetMinutes() != null) {
        UserGpTracker existing = trackerRepo.findById(id).orElseThrow();
        existing.setNotificationOffsetMinutes(sanitizeOffset(req.offsetMinutes()));
        trackerRepo.save(existing);
      }
      return gp.getId();
    }

    user.addTracker(gp, sanitizeOffset(req.offsetMinutes()));
    userRepo.save(user);
    return gp.getId();
  }

  @Transactional(readOnly = true)
  public List<TrackerResource> listFuture(Long userId) {
    LocalDate today = LocalDate.now();
    return trackerRepo.findByUser_Id(userId).stream()
        .filter(t -> t.getGrandPrix().getEndingDate() == null
                  || !t.getGrandPrix().getEndingDate().isBefore(today))
        .sorted(Comparator.comparing(
            t -> t.getGrandPrix().getStartingDate(),
            Comparator.nullsLast(Comparator.naturalOrder())))
        .map(t -> new TrackerResource(
            t.getGrandPrix().getId(),
            t.getGrandPrix().getGrandPrixCode(),
            t.getGrandPrix().getName(),
            t.getGrandPrix().getRound(),
            t.getGrandPrix().getStartingDate(),
            t.getGrandPrix().getEndingDate(),
            t.getNotificationOffsetMinutes()
        ))
        .toList();
  }

  public void update(UpdateTrackerRequest req) {
    if (req.offsetMinutes() == null) {
      throw new IllegalArgumentException("offsetMinutes est requis");
    }
    // trackerId == gpId
    UserGpTrackerId id = new UserGpTrackerId(req.userId(), req.trackerId());
    UserGpTracker tracker = trackerRepo.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Tracker introuvable"));

    if (tracker.getGrandPrix().getEndingDate() != null
        && tracker.getGrandPrix().getEndingDate().isBefore(LocalDate.now())) {
      throw new IllegalStateException("Grand Prix passé : modification interdite");
    }

    tracker.setNotificationOffsetMinutes(sanitizeOffset(req.offsetMinutes()));
    trackerRepo.save(tracker);
  }

  public void delete(DeleteTrackerRequest req) {
    User user = userRepo.findById(req.userId())
        .orElseThrow(() -> new EntityNotFoundException("Utilisateur introuvable"));
    user.removeTrackerByGpId(req.trackerId());
    userRepo.save(user);
  }

  private int sanitizeOffset(Integer offset) {
    int value = (offset == null) ? 10 : offset;
    if (value < 0) value = 0;
    if (value > 240) value = 240;
    return value;
  }
}