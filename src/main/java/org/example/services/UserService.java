package org.example.services;

import lombok.RequiredArgsConstructor;
import org.example.dtos.UserDto;
import org.example.entities.User;
import org.example.entities.UserRole;
import org.example.exceptions.UserException;
import org.example.interfaces.IUserRepository;
import org.example.interfaces.IUserRolesRepository;
import org.example.interfaces.IUserService;
import org.example.mapping.UserMapper;
import org.example.models.FileFormats;
import org.example.models.PaginationResponse;
import org.example.models.Roles;
import org.example.models.UserCreationModel;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final IUserRepository userRepo;
    private final UserMapper mapper;
    private final StorageService storageService;
    private final IUserRolesRepository rolesRepo;
    private final PasswordEncoder passwordEncoder;

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
        user.setRoles(List.of(rolesRepo.getByName(Roles.User.toString())));
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
