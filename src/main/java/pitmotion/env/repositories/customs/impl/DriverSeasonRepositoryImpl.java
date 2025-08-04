package pitmotion.env.repositories.customs.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import pitmotion.env.entities.*;
import pitmotion.env.repositories.customs.DriverSeasonRepositoryCustom;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DriverSeasonRepositoryImpl implements DriverSeasonRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<DriverSeason> findByDriverTeamAndYear(Driver driver, Team team, int year) {
        QDriverSeason ds = QDriverSeason.driverSeason;
        QTeamSeason ts   = QTeamSeason.teamSeason;
        QChampionship ch = QChampionship.championship;

        return Optional.ofNullable(
            queryFactory
              .selectFrom(ds)
              .join(ds.teamSeason, ts).fetchJoin()
              .join(ts.championship, ch).fetchJoin()
              .where(
                  ds.driver.eq(driver),
                  ts.team.eq(team),
                  ch.year.eq(year)
              )
              .fetchFirst()
        );
    }

    @Override
    public Optional<DriverSeason> findByDriverAndYear(Driver driver, int year) {
        QDriverSeason ds = QDriverSeason.driverSeason;
        QTeamSeason ts   = QTeamSeason.teamSeason;
        QChampionship ch = QChampionship.championship;

        return Optional.ofNullable(
            queryFactory
              .selectFrom(ds)
              .join(ds.teamSeason, ts).fetchJoin()
              .join(ts.championship, ch).fetchJoin()
              .where(
                  ds.driver.eq(driver),
                  ch.year.eq(year)
              )
              .fetchFirst()
        );
    }
}
