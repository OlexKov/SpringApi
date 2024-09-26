package org.example.interfaces;


import org.example.dtos.CartProductDto;
import org.example.dtos.ProductDto;
import org.example.dtos.UserDto;
import org.example.entities.User;
import org.example.models.CartProductModel;
import org.example.models.PaginationResponse;
import org.example.models.UserCreationModel;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

public interface IUserService extends UserDetailsService {
    Long create(UserCreationModel userModel);
    PaginationResponse<UserDto> getUsers(int page, int size);
    UserDto getById(Long id);
    UserDto getDtoByUserName(String userName);
    boolean deleteById(Long id) throws IOException;
    boolean update(UserCreationModel productModel) throws IOException;
    User getByUsername(String userName);
    UserDetailsService userDetailsService();
    Long addToFavorite(Long id);
    Long removeFromFavorite(Long id);
    PaginationResponse<ProductDto> getFavorite(int page,int size);
    int addToFavorite(Long[] ids);

    int addToCart(Long id);
    int removeFromCart(Long id);
    Iterable<CartProductDto> getCart();
    int addAllToCart(CartProductModel[] data);
    void setCartProductCount(Long id,int count);
}
