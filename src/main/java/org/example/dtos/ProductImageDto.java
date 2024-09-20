package org.example.dtos;

import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductImageDto {
    private Long id;
    private String name;
    private int priority;
    private LocalDateTime dateCreated;
    private Long productId;
}
