package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.dtos.CategoryDto;

@Data
@AllArgsConstructor
public class CategoryResponse {
    private Iterable<CategoryDto> categoryList;
    private long totalElements ;
}
