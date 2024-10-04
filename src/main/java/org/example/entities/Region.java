package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tbl_regions")
public class Region {
    @Id
    private String id;
    private String description;
    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area area;

    @ManyToOne
    @JoinColumn(name = "region_center_id", referencedColumnName = "id",nullable = true)
    private Settlement regionCenter;

    @OneToMany(mappedBy = "region", cascade = CascadeType.ALL)
    private Set<Settlement> settlements = new HashSet<>();
}