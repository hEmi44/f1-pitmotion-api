package pitmotion.env.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.UserGpTracker;
import pitmotion.env.entities.UserGpTrackerId;

import java.util.List;

public interface UserGpTrackerRepository extends JpaRepository<UserGpTracker, UserGpTrackerId> {
    List<UserGpTracker> findByUserId(Long userId);
}
