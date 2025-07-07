package com.example.user_service.service.interfaces;

import com.example.user_service.dto.user.UserResponseDto;
import com.example.user_service.dto.user.UserUpdateDto;
import com.example.user_service.entity.User;
import org.springframework.security.core.Authentication;

public interface UserService {
    User loadUserByUsername(String username);
    User loadUserById(Long id);
    UserResponseDto getCurrentUser(Authentication authentication);
    String getUserNameFromAuth(Authentication authentication);
    UserResponseDto updateUser(UserUpdateDto userUpdateDto, Authentication authentication);
}
