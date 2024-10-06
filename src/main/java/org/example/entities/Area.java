package org.example.entities;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tbl_areas")
public class Area {
    @Id
    private String id;
    private String description;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "settlement_id")
    private Settlement areaCenter;

    @OneToMany(mappedBy = "area",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Region> regions = new HashSet<>();
}
