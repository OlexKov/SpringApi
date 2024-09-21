package org.example.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dtos.ProductDto;
import org.example.interfaces.IAuthenticationService;
import org.example.interfaces.IUserService;
import org.example.models.AuthenticationResponse;
import org.example.models.PaginationResponse;
import org.example.models.SignInRequest;
import org.example.models.UserCreationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.ok().body(id.toString());
        }catch(Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value ="/sign-in", consumes = "multipart/form-data")
    public  ResponseEntity<AuthenticationResponse> signIn(@ModelAttribute @Valid SignInRequest request) {
        try{
            var response  = authenticationService.signIn(request);
            return ResponseEntity.ok().body(response);
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

    @PostMapping(value ="/remove-favorite/{id}")
    public  ResponseEntity<Long> removeFavorite(@PathVariable Long id ) {
        try{
            Long resultId = userService.removeFromFavorite(id);
            return ResponseEntity.ok().body(resultId);
        }catch(Exception e){
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping(value ="/get-favorites")
    public  ResponseEntity<PaginationResponse<ProductDto>> getFavorite() {
        try{
            return ResponseEntity.ok().body(userService.getFavorite());
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
