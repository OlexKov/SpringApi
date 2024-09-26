package org.example.mapping;
import org.example.dtos.CartProductDto;
import org.example.entities.CartProduct;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring",uses = {UserMapper.class})
public interface CartProductMapper {
    CartProductDto toDto(CartProduct product);
    List<CartProductDto> toDto(Iterable<CartProduct> product);
    default Long toId(CartProduct product) {
        return product.getUser() != null ? product.getUser().getId() : null;
    }
}
