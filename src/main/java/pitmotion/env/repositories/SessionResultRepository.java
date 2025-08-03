package pitmotion.env.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.DriverSeason;
import pitmotion.env.entities.GpSession;
import pitmotion.env.entities.SessionResult;

public interface SessionResultRepository extends JpaRepository<SessionResult, Long> {
  Optional<SessionResult> findByGpSessionAndDriverSeason(GpSession session, DriverSeason driverSeason);
  Optional<SessionResult> findTopByGpSessionOrderByLapTimeSecondsAsc(GpSession session);
}
