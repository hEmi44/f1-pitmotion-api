package pitmotion.env.services;

import java.util.Comparator;
import java.util.List;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pitmotion.env.http.resources.calendars.CalendarGrandPrixEntryResource;
import pitmotion.env.http.resources.calendars.CalendarResource;
import pitmotion.env.mappers.CalendarMapper;
import pitmotion.env.entities.GrandPrix;
import pitmotion.env.entities.GpSession;
import pitmotion.env.repositories.GrandPrixRepository;
import pitmotion.env.repositories.GpSessionRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

  private final GrandPrixRepository grandPrixRepository;
  private final GpSessionRepository gpSessionRepository;
  private final CalendarMapper mapper;

  public CalendarResource getSeasonCalendar(int year) {
    List<GrandPrix> gps = grandPrixRepository.findAll().stream()
        .filter(gp -> gp.getChampionship() != null && year == gp.getChampionship().getYear())
        .sorted(Comparator.comparing(GrandPrix::getRound))
        .toList();

    if (gps.isEmpty()) {
      throw new EntityNotFoundException("Saison introuvable: " + year);
    }

    List<CalendarGrandPrixEntryResource> entries = gps.stream()
        .map(gp -> {
          List<GpSession> sessions = gpSessionRepository.findByGrandPrix(gp).stream()
              .sorted(Comparator.comparing(GpSession::getDate))
              .toList();
          return mapper.toEntryWithSessions(gp, sessions);
        })
        .toList();

    return mapper.toCalendarResource(year, entries);
  }
}

