package org.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductImageDto {
    private Long id;
    private String name;
    private int priority;
    private LocalDateTime dateCreated;
    private Long productId;
}
