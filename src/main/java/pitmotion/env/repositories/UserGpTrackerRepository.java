package pitmotion.env.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pitmotion.env.entities.UserGpTracker;
import pitmotion.env.entities.UserGpTrackerId;

import java.util.List;

public interface UserGpTrackerRepository extends JpaRepository<UserGpTracker, UserGpTrackerId> {
  List<UserGpTracker> findByUser_Id(Long userId);
  List<UserGpTracker> findByGrandPrix_Id(Long gpId);
  }
