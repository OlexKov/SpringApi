package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.interfaces.IAuthenticationService;
import org.example.interfaces.IJwtService;
import org.example.interfaces.IUserService;
import org.example.models.AuthenticationResponse;
import org.example.models.SignInRequest;
import org.example.models.UserCreationModel;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;

    @Override
    public Long registration(UserCreationModel model) {
        return userService.create(model);
    }

    @Override
    public AuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));
        var user = userService.getByUsername(request.getUsername());
        var jwt = jwtService.generateToken(user);
        return new AuthenticationResponse(jwt);
    }
}
