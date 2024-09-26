package org.example.mapping;

import org.example.dtos.ProductDto;
import org.example.entities.Product;
import org.example.models.ProductCreationModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring",uses = {ProductImageMapper.class,UserMapper.class,CartProductMapper.class})
public interface ProductMapper {
    Product fromCreationModel(ProductCreationModel productModel);
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductDto toDto(Product product);
    List<ProductDto> toDto(Iterable<Product> product);
}

