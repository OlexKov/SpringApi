package org.example.dtos;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CategoryDto {
    private Long id;
    private String name;
    private String image;
    private String description;
    private LocalDateTime creationTime;
}
