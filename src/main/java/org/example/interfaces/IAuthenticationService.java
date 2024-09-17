package org.example.interfaces;

import org.example.models.AuthenticationResponse;
import org.example.models.SignInRequest;
import org.example.models.UserCreationModel;

public interface IAuthenticationService {
    Long registration(UserCreationModel model);
    AuthenticationResponse signIn(SignInRequest request);
}
