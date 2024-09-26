package org.example.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tbl_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 4000)
    private String description;

    @Column(name = "date_created")
    private LocalDateTime creationTime;

    @Column(nullable = false, precision = 2)
    private double price;

    @Column(nullable = false, precision = 2)
    private double discount;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @BatchSize(size = 20)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<CartProduct> inUsersCart = new HashSet<>();

    @BatchSize(size = 20)
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private Set<ProductImage> images = new HashSet<>();

    @BatchSize(size = 20)
    @ManyToMany(mappedBy = "favoriteProducts")
    private Set<User> favoriteInUsers = new HashSet<>();
}
