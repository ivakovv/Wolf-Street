package com.example.user_service.mapper;

import com.example.user_service.dto.event.UserRegistredEvent;
import com.example.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MapperToUserRegistredEvent {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "username", source = "username")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "firstname", source = "firstname")
    @Mapping(target = "lastname", source = "lastname")
    @Mapping(target = "phone", source = "phone")
    @Mapping(target = "created_at", source = "created_at")
    @Mapping(target = "updated_at", source = "updated_at")
    UserRegistredEvent mapToUserRegistredEvent(User user);
}
