package com.example.user_service.mapper;

import com.example.user_service.dto.user.UserUpdateDto;
import com.example.user_service.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MapperUpdateUser {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "created_at", ignore = true)
    @Mapping(target = "updated_at", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    void mapUpdateUser(@MappingTarget User user, UserUpdateDto userUpdateDto);
}
