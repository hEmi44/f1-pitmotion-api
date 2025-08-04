package pitmotion.env.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "driver_aliases",
       uniqueConstraints = @UniqueConstraint(columnNames = "alias"))
@Getter @Setter @NoArgsConstructor
public class DriverAlias {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String alias;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "driver_id",
                foreignKey = @ForeignKey(name = "fk_alias_driver"),
                nullable = false)
    private Driver driver;
}
