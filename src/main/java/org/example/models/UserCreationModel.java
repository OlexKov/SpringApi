package org.example.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserCreationModel {
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String surname;
    private Date birthdate;
    private MultipartFile file;
    private String recaptchaToken;
}
