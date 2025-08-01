package pitmotion.env.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.GpSession;
import pitmotion.env.entities.GrandPrix;
import pitmotion.env.enums.SessionType;

public interface GpSessionRepository extends JpaRepository<GpSession, Long> {
    Optional<GpSession> findByGrandPrixAndType(GrandPrix grandPrix, SessionType type);
}
