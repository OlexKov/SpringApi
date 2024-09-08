package org.example.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String image;
    private String description;
    private LocalDateTime creationTime;
    private double price;
    private double discount;
}
