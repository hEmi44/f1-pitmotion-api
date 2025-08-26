package pitmotion.env.services;

import java.util.Comparator;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pitmotion.env.http.resources.races.RaceSessionsResource;
import pitmotion.env.mappers.RaceMapper;
import pitmotion.env.entities.GrandPrix;
import pitmotion.env.entities.GpSession;
import pitmotion.env.repositories.GrandPrixRepository;
import pitmotion.env.repositories.GpSessionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RaceService {

  private final GrandPrixRepository grandPrixRepository;
  private final GpSessionRepository gpSessionRepository;
  private final RaceMapper mapper;

  public RaceSessionsResource getPlannedSessions(String raceCode) {
    GrandPrix gp = grandPrixRepository.findAll().stream()
        .filter(g -> raceCode.equalsIgnoreCase(g.getGrandPrixCode()))
        .max(Comparator.comparing(g -> g.getChampionship() != null ? g.getChampionship().getYear() : -1))
        .orElseThrow(() -> new EntityNotFoundException("Grand Prix introuvable: " + raceCode));

    List<GpSession> sessions = gpSessionRepository.findByGrandPrix(gp).stream()
        .sorted(Comparator.comparing(GpSession::getDate))
        .toList();

    return mapper.toRaceSessionsResource(gp, sessions);
  }
}
