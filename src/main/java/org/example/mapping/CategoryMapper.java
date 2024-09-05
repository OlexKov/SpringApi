package org.example.mapping;

import org.example.dtos.CategoryDto;
import org.example.entities.Category;
import org.example.models.CategoryCreationModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(target = "image", ignore = true)
    Category fromCreationModel(CategoryCreationModel categoryModel);
    CategoryDto toDto(Category category);
    List<CategoryDto> toDto(Iterable<Category> category);
}