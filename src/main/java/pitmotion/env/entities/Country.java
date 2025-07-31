package pitmotion.env.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "countries")
@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class Country {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "countries_seq")
  @SequenceGenerator(
      name = "countries_seq",
      sequenceName = "countries_seq",
      allocationSize = 1
  )
  private Long id;

  @Column(name = "name_fr")
  private String nameFr;
  @Column(name = "name_en")
  private String nameEn;
  @Column(name = "code_iso2")
  private String codeIso2;
  @Column(name = "code_iso3")
  private String codeIso3;

  @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
  @JsonBackReference
  private List<Driver> drivers = new ArrayList<>();

  @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
  @JsonBackReference
  private List<Team> teams = new ArrayList<>();

  @OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
  @JsonBackReference
  private List<Circuit> circuits = new ArrayList<>();
}
