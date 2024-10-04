package org.example.entities;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
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

    @OneToOne
    @JoinColumn(name = "area_center_id", referencedColumnName = "id")
    private Settlement areaCenter;

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL)
    private Set<Region> regions = new HashSet<>();

//    @BatchSize(size = 20)
//    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL)
//    private Set<Settlement>  settlements = new HashSet<>();
}
