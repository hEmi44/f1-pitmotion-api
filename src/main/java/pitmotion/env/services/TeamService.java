package pitmotion.env.services;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pitmotion.env.http.resources.teams.TeamListResource;
import pitmotion.env.http.resources.teams.TeamResource;
import pitmotion.env.mappers.TeamMapper;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.Team;
import pitmotion.env.repositories.DriverSeasonRepository;
import pitmotion.env.repositories.TeamRepository;
import pitmotion.env.repositories.TeamSeasonRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

  private final TeamRepository teamRepository;
  private final TeamSeasonRepository teamSeasonRepository;
  private final DriverSeasonRepository driverSeasonRepository;
  private final TeamMapper mapper;

  public TeamListResource listTeams() {
    Integer refYear = teamSeasonRepository.findAll().stream()
        .map(ts -> ts.getChampionship() != null ? ts.getChampionship().getYear() : null)
        .filter(Objects::nonNull)
        .max(Integer::compareTo)
        .orElse(null);

    List<Team> teams = teamRepository.findAll().stream()
        .sorted(Comparator.comparing(Team::getName, Comparator.nullsLast(String::compareToIgnoreCase)))
        .toList();

    Map<Long, List<DriverSeason>> driversByTeamForRefYear = driverSeasonRepository.findAll().stream()
        .filter(ds -> ds.getTeamSeason() != null
            && ds.getTeamSeason().getChampionship() != null
            && Objects.equals(refYear, ds.getTeamSeason().getChampionship().getYear()))
        .collect(Collectors.groupingBy(ds -> ds.getTeamSeason().getTeam() != null ? ds.getTeamSeason().getTeam().getId() : -1L));

    return mapper.toTeamListResource(teams, refYear, driversByTeamForRefYear);
  }

  public TeamResource getTeam(String code) {
    Team team = teamRepository.findByTeamCode(code)
        .orElseThrow(() -> new EntityNotFoundException("Team introuvable: " + code));

    Map<Integer, List<DriverSeason>> byYear = driverSeasonRepository.findAll().stream()
        .filter(ds -> ds.getTeamSeason() != null
            && ds.getTeamSeason().getTeam() != null
            && Objects.equals(ds.getTeamSeason().getTeam().getId(), team.getId())
            && ds.getTeamSeason().getChampionship() != null)
        .collect(Collectors.groupingBy(ds -> ds.getTeamSeason().getChampionship().getYear()));

    return mapper.toTeamResource(team, byYear);
  }
}
