package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dtos.CartProductDto;
import org.example.dtos.CategoryDto;
import org.example.dtos.ProductDto;
import org.example.dtos.UserDto;
import org.example.entities.CartProduct;
import org.example.entities.Product;
import org.example.entities.User;
import org.example.entities.UserRole;
import org.example.exceptions.ProductException;
import org.example.exceptions.UserException;
import org.example.interfaces.*;
import org.example.mapping.CartProductMapper;
import org.example.mapping.ProductMapper;
import org.example.mapping.UserMapper;
import org.example.models.*;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepo;
    private final UserMapper mapper;
    private final ProductMapper ProductMapper;
    private final StorageService storageService;
    private final IUserRolesRepository rolesRepo;
    private final PasswordEncoder passwordEncoder;
    private final IProductRepository productRepo;
    private final ICartProductRepository cartProductRepo;
    private final CartProductMapper cartProductMapper;

    @Override
    public Long create(UserCreationModel userModel) {
        if(userRepo.getByUsername(userModel.getUsername()) != null)
        {
          throw new UserException("User with that name already exists ");
        }
        if(userRepo.getByEmail(userModel.getEmail()) != null)
        {
            throw new UserException("User with that email already exists ");
        }

        var user = mapper.fromCreationModel(userModel);
        user.setRoles(Set.of(rolesRepo.getByName(Roles.User.toString())));
        //
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        //
        user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        try{
            user.setImage(storageService.saveImage(userModel.getFile(), FileFormats.WEBP));
            return  userRepo.save(user).getId();
        }
        catch (Exception e){
            throw new UserException("Error save user");
        }
    }

    @Override
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    @Override
    public Long addToFavorite(Long id) {
        var optProduct = productRepo.findById(id);
        if(optProduct.isPresent()){
            Long userId =  ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
            var optUser = userRepo.findById(userId);
            if(optUser.isPresent()){
                var user = optUser.get();
                var favoriteProducts = user.getFavoriteProducts();
                favoriteProducts.add(optProduct.get());
                user.setFavoriteProducts(favoriteProducts);
                userRepo.save(user);
                return id;
            }
            else{
                throw new UserException("Invalid user id");
            }
        }
        else{
            throw new ProductException("Invalid product id");
        }
    }

    @Override
    public int addToFavorite(Long[] ids) {
        Long userId =  ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        var optUser = userRepo.findById(userId);
        Set<Product> favoriteProducts = new HashSet<>();
        if(optUser.isPresent()){
             var products = productRepo.getProducts(ids);
             User user = optUser.get();
             favoriteProducts = user.getFavoriteProducts();
             if(!products.isEmpty()){
                favoriteProducts.addAll(products);
                user.setFavoriteProducts(favoriteProducts);
                userRepo.save(user);
                return favoriteProducts.size();
            }
        }
        return favoriteProducts.size();
    }

    @Override
    @Transactional
    public int addToCart(Long id) {
        Long userId =  ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        var optUser = userRepo.findById(userId);
        Set<CartProduct> cart = Set.of();
        if(optUser.isPresent()){
            var product = productRepo.findById(id);
            if(product.isPresent()){
                User user = optUser.get();
                cart = user.getCart();
                cart.add(new CartProduct(null,optUser.get(),product.get(),1));
            }
            else{
                throw new ProductException("Invalid product id");
            }
        }
        return cart.size();
    }

    @Override
    @Transactional
    public int removeFromCart(Long id) {
        Long userId =  ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        var optUser = userRepo.findById(userId);
        if(optUser.isPresent()){
            User user = optUser.get();
            var cart = user.getCart();
            cart.removeIf(x->Objects.equals(x.getProduct().getId(), id));
            return cart.size();
        }
        return 0;
    }

    @Override
    public Iterable<CartProductDto> getCart() {
        Long userId =  ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        var optUser = userRepo.findById(userId);
        if(optUser.isPresent()) {
            User user = optUser.get();
            return cartProductMapper.toDto(user.getCart());
        }
        return null;
    }

    @Override
    @Transactional
    public int addAllToCart(CartProductModel[] data) {
        Long userId =  ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        var optUser = userRepo.findById(userId);
        Set<CartProduct> cart = Set.of();
        if(optUser.isPresent()){
            User user = optUser.get();
            cart = user.getCart();
            for (var item:data){
                var product = productRepo.findById(item.getId()) ;
                if(product.isPresent() && cart.stream().noneMatch(x-> Objects.equals(x.getProduct().getId(), item.getId()))){
                    cart.add(new CartProduct(null, user, product.get(), item.getCount()));
                }
            }
        }
        return cart.size();
    }

    @Override
    public void setCartProductCount(Long id, int count) {
        Long userId =  ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        var optUser = userRepo.findById(userId);
        if(optUser.isPresent()) {
            User user = optUser.get();
            var cartProduct = user.getCart().stream().filter(x->Objects.equals(x.getProduct().getId(),id)).findFirst().orElse(null);
            if(cartProduct != null){
                cartProduct.setCount(count);
                userRepo.save(user);
            }
        }
    }

    @Override
    public Long removeFromFavorite(Long id) {
        Long userId =  ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        var optUser = userRepo.findById(userId);
        if(optUser.isPresent()){
            var user = optUser.get();
            var favoriteProducts = user.getFavoriteProducts();
            favoriteProducts = favoriteProducts.stream().filter(x-> !Objects.equals(x.getId(), id)).collect(Collectors.toSet());
            user.setFavoriteProducts(favoriteProducts);
            userRepo.save(user);
            return id;
        }
        else{
            throw new UserException("Invalid user id");
        }
    }

    @Override
    public PaginationResponse<ProductDto> getFavorite(int page,int size) {
        Long userId =  ((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        var optUser = userRepo.findById(userId);
        if(optUser.isPresent()){
            var allFProducts = optUser.get().getFavoriteProducts();
            var total = allFProducts.size();
            var favoriteProducts = allFProducts.stream().skip((long) (page-1) * size).limit(size).toList();
            return new PaginationResponse<ProductDto>(ProductMapper.toDto(favoriteProducts),total);
        }
        else{
            throw new UserException("Invalid user id");
        }
    }


    @Override
    public PaginationResponse<UserDto> getUsers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(
                page, size, Sort.by("id"));
        Page<User> usersPage = userRepo.findAll(pageRequest);
        Iterable<UserDto> users = mapper.toDto(usersPage.getContent());
        return  new PaginationResponse<UserDto>(users,usersPage.getTotalElements());
    }

    @Override
    public UserDto getById(Long id) {
        Optional<User> user = userRepo.findById(id);
        if(user.isPresent()){
            return mapper.toDto(user.get());
        }
        else{
            throw new UserException("Invalid user id");
        }
    }

    @Override
    public UserDto getDtoByUserName(String userName) {
        return mapper.toDto(userRepo.getByUsername(userName));
    }

    @Override
    public User getByUsername(String userName) {
        return loadUserByUsername(userName);
    }

    @Override
    public User loadUserByUsername(String userName) {
        User user = userRepo.getByUsername(userName);
        if(user == null) throw new UsernameNotFoundException("User not found");
       return user;
    }

    @Override
    public boolean deleteById(Long id) throws IOException {
        Optional<User> optUser =  userRepo.findById(id);
        boolean isPresent = optUser.isPresent();
        if(isPresent){
            User user = optUser.get();
            userRepo.delete(user);
            storageService.deleteImage(user.getImage());
        }
        return  isPresent;
    }

    @Override
    public boolean update(UserCreationModel productModel) throws IOException {
        return false;
    }


}
