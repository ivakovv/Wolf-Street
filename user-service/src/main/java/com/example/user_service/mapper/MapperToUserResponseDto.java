package com.example.user_service.mapper;

import com.example.user_service.dto.user.UserResponseDto;
import com.example.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapperToUserResponseDto {
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstname", source = "firstname")
    @Mapping(target = "lastname", source = "lastname")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "created_at", source = "created_at")
    @Mapping(target = "updated_at", source = "updated_at")
    UserResponseDto mapToUserResponseDto(User user);
}

