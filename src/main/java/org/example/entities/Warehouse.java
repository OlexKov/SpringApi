package org.example.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tbl_warehouses")
public class Warehouse {
    @Id
    private String id;
    private String description;
    private String phone;

    @ManyToOne
    @JoinColumn(name = "settlement_id",nullable = true)
    private Settlement settlement;
}
