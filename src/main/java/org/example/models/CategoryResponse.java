package org.example.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.dtos.CategoryDto;

@Data
@AllArgsConstructor
public class CategoryResponse {
    private Iterable<CategoryDto> categoryList;
    private long totalElements ;
}
