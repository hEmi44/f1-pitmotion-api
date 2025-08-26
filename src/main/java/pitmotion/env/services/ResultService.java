package pitmotion.env.services;

import java.util.Comparator;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pitmotion.env.http.resources.results.SessionResultResource;
import pitmotion.env.mappers.ResultMapper;
import pitmotion.env.entities.GrandPrix;
import pitmotion.env.entities.GpSession;
import pitmotion.env.entities.SessionResult;
import pitmotion.env.enums.SessionType;
import pitmotion.env.repositories.GrandPrixRepository;
import pitmotion.env.repositories.GpSessionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ResultService {

  private final GrandPrixRepository grandPrixRepository;
  private final GpSessionRepository gpSessionRepository;
  private final ResultMapper mapper;

  public SessionResultResource getSessionResults(int year, int round, SessionType session) {
    GrandPrix gp = grandPrixRepository.findAll().stream()
        .filter(g -> g.getChampionship() != null
            && year == g.getChampionship().getYear()
            && g.getRound() != null
            && g.getRound() == round)
        .findFirst()
        .orElseThrow(() -> new EntityNotFoundException(
            "Grand Prix introuvable: year=" + year + ", round=" + round
        ));

    GpSession gpSession = gpSessionRepository.findByGrandPrixAndType(gp, session)
        .orElseThrow(() -> new EntityNotFoundException("Session introuvable: " + session));

    List<SessionResult> results = gpSession.getResults().stream()
        .sorted(
            Comparator
                .comparing((SessionResult r) -> r.getPosition() == null ? Integer.MAX_VALUE : r.getPosition())
                .thenComparing(r -> r.getPoints() == null ? 0 : r.getPoints(), Comparator.reverseOrder())
        )
        .toList();

    return mapper.toResult(gp, gpSession, results);
  }
}