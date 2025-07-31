package pitmotion.env.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pitmotion.env.entities.Country;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findByNameFr(String nameFr);
    Optional<Country> findByNameEn(String nameEn);
}
