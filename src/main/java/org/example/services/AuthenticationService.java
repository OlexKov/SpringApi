package org.example.services;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.entities.User;
import org.example.interfaces.*;
import org.example.models.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {
    private final IUserService userService;
    private final AuthenticationManager authenticationManager;
    private final IJwtService jwtService;
    private final IStorageService storageService;
    private final IUserRolesRepository rolesRepo;
    private final IUserRepository userRepo;
    private final RestTemplateBuilder restTemplateBuilder;

    @Value("${recaptcha.secret.key}")
    private String secretKey;

    @Value("${recaptcha.api.url}")
    private String recaptchaApiUrl;

    @Value("${google.api.userinfo}")
    private String googleUserInfoUrl;

    @Override
    public Long registration(UserCreationModel model) {
        if(checkRecaptcha(model.getRecaptchaToken())){
            return userService.create(model);
        }
        return null;
    }

    @Override
    public AuthenticationResponse signIn(SignInRequest request) {
        if(checkRecaptcha(request.getRecaptchaToken())){
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            ));
            var user = userService.getByUsername(request.getUsername());
            var jwt = jwtService.generateToken(user);
            return new AuthenticationResponse(jwt);
        }
        return null;
    }

    @Override
    public AuthenticationResponse signInGoogle(String access_token) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + access_token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(googleUserInfoUrl, HttpMethod.GET, entity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> userInfo = mapper.readValue(response.getBody(), new TypeReference<Map<String, String>>() {});
            User user = userRepo.getByEmail(userInfo.get("email"));
            if(user == null){
                var userRole = rolesRepo.getByName(Roles.User.toString());
                user = new User(
                        0L,
                        userInfo.get("email"),
                        " ",
                        userInfo.get("given_name"),
                        userInfo.get("family_name"),
                        userInfo.get("email"),
                        new Date(),
                        storageService.saveImage(userInfo.get("picture"), FileFormats.WEBP),
                        true,true,true,true,
                        Set.of(),
                        Set.of(userRole),
                        Set.of()
                );
              user = userRepo.saveAndFlush(user);
            }
            var jwt = jwtService.generateToken(user);
            return new AuthenticationResponse(jwt);
        }
         return  null;
    }

    @Override
    public boolean checkRecaptcha(String rToken) {
        final RestTemplate restTemplate = restTemplateBuilder.build();
        String url = String.format("%s?secret=%s&response=%s", recaptchaApiUrl, secretKey, rToken);
            try {
                ResponseEntity<RecaptchaResponse> response = restTemplate.exchange(url, HttpMethod.POST, null, RecaptchaResponse.class);
                RecaptchaResponse body = response.getBody();
                assert body != null;
                return  body.isSuccess() && body.getScore() >= 0.5;
            } catch (RestClientException e) {
                e.printStackTrace();
                return false;
            }
        }

}
