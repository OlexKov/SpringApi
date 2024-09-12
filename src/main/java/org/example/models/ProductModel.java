package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    private Long id;
    private String name;
    private String description;
    private double price;
    private double discount;
    private Long categoryId;
}
