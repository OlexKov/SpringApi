package org.example.interfaces;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.models.AuthenticationResponse;
import org.example.models.SignInRequest;
import org.example.models.UserCreationModel;

import java.io.IOException;

public interface IAuthenticationService {
    Long registration(UserCreationModel model);
    AuthenticationResponse signIn(SignInRequest request);
    AuthenticationResponse signInGoogle(String code) throws IOException;
    boolean checkRecaptcha(String rToken);
}
