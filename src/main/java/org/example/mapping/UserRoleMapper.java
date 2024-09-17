package org.example.mapping;

import org.example.dtos.UserDto;
import org.example.dtos.UserRoleDto;
import org.example.entities.User;
import org.example.entities.UserRole;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {
    UserRoleDto toDto(UserRole userRole);
    List<UserRoleDto> toDto(Iterable<UserRole> users);
}
