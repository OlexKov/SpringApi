package org.example.dtos;


import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class UserDto {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String surname;
    private Date birthdate;
    private String image;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    private Set<UserRoleDto> roles;
}
