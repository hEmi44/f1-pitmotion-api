package pitmotion.env.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import pitmotion.env.entities.UserGpTracker;
import pitmotion.env.entities.UserGpTrackerId;

import java.util.List;

public interface UserGpTrackerRepository extends JpaRepository<UserGpTracker, UserGpTrackerId> {
    List<UserGpTracker> findByUserId(Long userId);
    @Query("select t from UserGpTracker t where t.grandPrix.id = :gpId and t.enabled = true")
    List<UserGpTracker> findActiveByGrandPrixId(Long gpId);
}
