package org.example.mapping;

import org.example.dtos.ProductDto;
import org.example.entities.Product;
import org.example.models.ProductCreationModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "image", ignore = true)
    Product fromCreationModel(ProductCreationModel productModel);
    ProductDto toDto(Product product);
    List<ProductDto> toDto(Iterable<Product> product);
}

