package org.example.mapping;

import org.example.dtos.ProductImageDto;
import org.example.entities.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {
    @Mapping(target = "productId", source = "product.id")
    ProductImageDto toDto(ProductImage image);
    List<ProductImageDto> toDto(Iterable<ProductImage> product);
}
