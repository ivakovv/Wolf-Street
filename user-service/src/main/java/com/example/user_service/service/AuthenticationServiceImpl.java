package com.example.user_service.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.user_service.dto.auth.AuthenticationResponseDto;
import com.example.user_service.dto.auth.LoginRequestDto;
import com.example.user_service.dto.auth.RegistrationRequestDto;
import com.example.user_service.entity.Token;
import com.example.user_service.entity.User;
import com.example.user_service.mapper.MapperToUser;
import com.example.user_service.repository.TokenRepository;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.interfaces.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final MapperToUser mapperToUser;

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
        userRepository.save(user);
    }

    @Override
    public AuthenticationResponseDto authenticate(LoginRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.username(),
                request.password()
        ));
        User user = userRepository.findByUsername(request.username()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователь с именем %s не найден!", request.username())));
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllToken(user);
        saveUserToken(accessToken, refreshToken, user);
        return new AuthenticationResponseDto(accessToken, refreshToken);
    }

    @Override
    public ResponseEntity<AuthenticationResponseDto> refreshToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorizationHeader.substring(7);
        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Пользователь с именем %s не найден!", username)));
        if (jwtService.isValidRefresh(token, user)) {
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            revokeAllToken(user);
            saveUserToken(accessToken, refreshToken, user);
            return new ResponseEntity<>(new AuthenticationResponseDto(accessToken, refreshToken), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private void revokeAllToken(User user) {
        List<Token> validTokens = tokenRepository.findAllByUserId(user.getId());
        if(!validTokens.isEmpty()){
            validTokens.forEach(t -> t.setIsLoggedOut(true));
        }
        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String accessToken, String refreshToken, User user) {
        Token token = Token.builder()
                .user(user)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isLoggedOut(false)
                .build();
        tokenRepository.save(token);
    }
}
