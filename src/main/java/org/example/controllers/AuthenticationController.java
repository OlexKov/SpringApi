package org.example.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dtos.CartProductDto;
import org.example.dtos.ProductDto;
import org.example.interfaces.IAuthenticationService;
import org.example.interfaces.IUserService;
import org.example.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "api/auth",produces = "application/json")
public class AuthenticationController {
    private final IAuthenticationService authenticationService;
    private final IUserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<String> signUp(@ModelAttribute @Valid UserCreationModel model) {
        try{
            Long id  = authenticationService.registration(model);
            return id != null? ResponseEntity.ok().body(id.toString()):ResponseEntity.badRequest().body("Error reCaptcha validation");
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value ="/sign-in", consumes = "multipart/form-data")
    public  ResponseEntity<AuthenticationResponse> signIn(@ModelAttribute @Valid SignInRequest request) {
        try{
            var response  = authenticationService.signIn(request);
            return response != null ?ResponseEntity.ok().body(response):ResponseEntity.badRequest().body(null);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping(value ="/sign-in/google")
    public  ResponseEntity<AuthenticationResponse> signInGoogle(@RequestBody Map<String, String> request) {
        try{
            var response  = authenticationService.signInGoogle(request.get("token"));
            return response != null? ResponseEntity.ok().body(response) : ResponseEntity.badRequest().body(null);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping(value ="/add-favorite/{id}")
    public  ResponseEntity<Long> addFavorite(@PathVariable Long id ) {
        try{
            Long resultId = userService.addToFavorite(id);
            return ResponseEntity.ok().body(resultId);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping(value ="/add-favorite")
    public  ResponseEntity<Integer> addFavorites(@RequestBody Long[] ids ) {
        try{
            int result = userService.addToFavorite(ids);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping(value ="/remove-favorite/{id}")
    public  ResponseEntity<Long> removeFavorite(@PathVariable Long id ) {
        try{
            Long resultId = userService.removeFromFavorite(id);
            return ResponseEntity.ok().body(resultId);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping(value ="/get-favorites/{page}/{size}")
    public  ResponseEntity<PaginationResponse<ProductDto>> getFavorite(@PathVariable int page,  @PathVariable int size) {
        try{
            return ResponseEntity.ok().body(userService.getFavorite(page,size));
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(null);
        }
    }



    @PostMapping(value ="/add-to-cart/{id}")
    public  ResponseEntity<Integer> addToCart(@PathVariable Long id ) {
        try{
            int resultId = userService.addToCart(id);
            return ResponseEntity.ok().body(resultId);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PostMapping(value ="/add-to-cart")
    public  ResponseEntity<Integer> addAllToCart(@RequestBody CartProductModel[] data ) {
        try{
            int result = userService.addAllToCart(data);
            return ResponseEntity.ok().body(result);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping(value ="/remove-from-cart/{id}")
    public  ResponseEntity<Integer> removeFromCart(@PathVariable Long id ) {
        try{
            int resultId = userService.removeFromCart(id);
            return ResponseEntity.ok().body(resultId);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping(value ="/get-cart")
    public  ResponseEntity<Iterable<CartProductDto>> getCart() {
        try{
            return ResponseEntity.ok().body(userService.getCart());
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping(value ="/set-count/{id}/{count}")
    public  ResponseEntity<Iterable<CartProductDto>> setCount(@PathVariable Long id,@PathVariable int count) {
        try{
            userService.setCartProductCount(id,count);
            return ResponseEntity.ok().body(null);
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
