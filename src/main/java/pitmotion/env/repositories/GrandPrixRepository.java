package pitmotion.env.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.GrandPrix;

import java.time.Instant;
import java.util.Optional;

public interface GrandPrixRepository extends JpaRepository<GrandPrix, Long> {
    Optional<GrandPrix> findByGrandPrixCode(String code);
    Optional<GrandPrix> findFirstByStartingDateAfterOrderByStartingDateAsc(Instant now);
}
