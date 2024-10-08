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
@Table(name="tbl_settlements")
public class Settlement {
    @Id
    private String id;
    private String description;
    private String settlementTypeDescription;

    @OneToOne(mappedBy = "areaCenter", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Area centerArea ;

    @OneToOne(mappedBy = "regionCenter", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Region centerRegion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    @OneToMany(mappedBy = "settlement", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Warehouse> warehouses = new HashSet<>();
}
