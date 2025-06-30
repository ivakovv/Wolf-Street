package com.example.user_service.service;

import com.example.user_service.dto.user.UserResponseDto;
import com.example.user_service.dto.user.UserUpdateDto;
import com.example.user_service.entity.User;
import com.example.user_service.mapper.MapperToUserResponseDto;
import com.example.user_service.mapper.MapperUpdateUser;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final MapperToUserResponseDto mapperToUserResponseDto;
    private final MapperUpdateUser mapperUpdateUser;
    public User loadUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователь с именем: %s не найден!", username)));
    }
    public User loadUserById(Long id){
        return userRepository.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователь с id: %d не найден!", id)));
    }
    public UserResponseDto getCurrentUser(Authentication authentication){
        User user = loadUserByUsername(getUserNameFromAuth(authentication));
        return mapperToUserResponseDto.mapToUserResponseDto(user);
    }
    public UserResponseDto updateUser(UserUpdateDto userUpdateDto, Authentication authentication){
        User user = loadUserByUsername(getUserNameFromAuth(authentication));
        mapperUpdateUser.mapUpdateUser(user, userUpdateDto);
        User savedUser = userRepository.save(user);
        return mapperToUserResponseDto.mapToUserResponseDto(savedUser);
    }
    public String getUserNameFromAuth(Authentication authentication){
        return ((UserDetails)authentication.getPrincipal()).getUsername();
    }
}
