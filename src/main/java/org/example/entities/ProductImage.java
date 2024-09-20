package org.example.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;



@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tbl_product_images")
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String name;

    private int priority;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime dateCreated;

    private boolean isDelete;

    @ManyToOne
    @JoinColumn(name="product_id", nullable = false)
    private Product product;

}
