package org.example.mapping;

import org.example.dtos.ProductDto;
import org.example.entities.Product;
import org.example.models.ProductCreationModel;
import org.example.models.ProductUpdateModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    Product fromCreationModel(ProductCreationModel productModel);
    @Mapping(target = "images", ignore = true)
    Product fromUpdateModel(ProductUpdateModel productModel);
    @Mapping(target = "categoryId", source = "category.id")
    ProductDto toDto(Product product);
    List<ProductDto> toDto(Iterable<Product> product);
}

