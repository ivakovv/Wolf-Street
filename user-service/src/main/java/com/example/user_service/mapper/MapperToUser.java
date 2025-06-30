package com.example.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.user_service.dto.auth.RegistrationRequestDto;
import com.example.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface MapperToUser {
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstname", source = "firstname")
    @Mapping(target = "lastname", source = "lastname")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "updated_at", ignore = true)
    User mapToUser(RegistrationRequestDto registrationRequestDto);
}
