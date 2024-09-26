package org.example.mapping;
import org.example.dtos.UserDto;
import org.example.entities.User;
import org.example.models.UserCreationModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;


@Mapper(componentModel = "spring",uses = {UserRoleMapper.class})
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    User fromCreationModel(UserCreationModel userModel);
    UserDto toDto(User user);
    List<UserDto> toDto(Iterable<User> users);
    default Long userToId(User user) {
        return user != null ? user.getId() : null;
    }
    List<Long> usersToIds(Iterable<User> users);
}
