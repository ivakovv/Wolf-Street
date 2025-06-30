package com.example.user_service.service.auth;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.user_service.dto.auth.AuthenticationResponseDto;
import com.example.user_service.dto.auth.ChangePasswordRequestDto;
import com.example.user_service.dto.auth.LoginRequestDto;
import com.example.user_service.dto.auth.RegistrationRequestDto;
import com.example.user_service.entity.User;
import com.example.user_service.enums.RoleType;
import com.example.user_service.mapper.MapperToUser;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.interfaces.AuthenticationService;
import com.example.user_service.service.interfaces.UserRoleService;
import com.example.user_service.service.interfaces.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final MapperToUser mapperToUser;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void register(RegistrationRequestDto request) {
        userRepository.findByUsername(request.username()).ifPresent(user ->{
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с таким username уже существует");
        });
        userRepository.findByEmail(request.email()).ifPresent(user ->{
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Пользователь с таким email уже существует");
        });
        User user = mapperToUser.mapToUser(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        User savedUser = userRepository.save(user);
        userRoleService.addRoleForUser(savedUser, RoleType.TRADER);
        eventPublisher.publishEvent(savedUser);
    }

    @Override
    public AuthenticationResponseDto authenticate(LoginRequestDto request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.username(),
                    request.password()
            ));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверное имя пользователя или пароль", e);
        }
        User user = userRepository.findByUsername(request.username()).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        return new AuthenticationResponseDto(accessToken, refreshToken);
    }

    @Override
    public ResponseEntity<AuthenticationResponseDto> refreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorizationHeader.substring(7);
        String userIdFromToken = jwtService.extractUserId(token);
        User user = userService.loadUserById(Long.valueOf(userIdFromToken));
        if (jwtService.isValidRefresh(token)) {
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            return new ResponseEntity<>(new AuthenticationResponseDto(accessToken, refreshToken), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    public void changePassword(ChangePasswordRequestDto request, Authentication authentication){
        User user = userService.loadUserByUsername(((UserDetails)authentication.getPrincipal()).getUsername());
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный текущий пароль");
        }
        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Новый пароль не должен совпадать со старым");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }
}
