package org.example.controllers;

import jakarta.validation.Valid;
import org.example.interfaces.IAuthenticationService;
import org.example.models.AuthenticationResponse;
import org.example.models.SignInRequest;
import org.example.models.UserCreationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/auth",produces = "application/json")
public class AuthenticationController {
    @Autowired
    private  IAuthenticationService authenticationService;

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
}
