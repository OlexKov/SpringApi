package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


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
    private String regionType;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "area_id")
    private Area area;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "settlement_id",nullable = true)
    private Settlement regionCenter;

    @OneToMany(mappedBy = "region",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Settlement> settlements = new HashSet<>();
}