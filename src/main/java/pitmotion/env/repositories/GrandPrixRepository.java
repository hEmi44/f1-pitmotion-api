package pitmotion.env.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.GrandPrix;

import java.time.LocalDate;
import java.util.Optional;

public interface GrandPrixRepository extends JpaRepository<GrandPrix, Long> {
    Optional<GrandPrix> findFirstByStartingDateAfterOrderByStartingDateAsc(LocalDate date);
    Optional<GrandPrix> findByGrandPrixCode(String code);
  }
