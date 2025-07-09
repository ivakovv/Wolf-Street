package com.example.user_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.example.user_service.dto.auth.AuthenticationResponseDto;
import com.example.user_service.dto.auth.LoginRequestDto;
import com.example.user_service.dto.auth.RegistrationRequestDto;
import com.example.user_service.entity.User;
import com.example.user_service.enums.RoleType;
import com.example.user_service.mapper.MapperToUser;
import com.example.user_service.repository.UserRepository;
import com.example.user_service.service.auth.AuthenticationServiceImpl;
import com.example.user_service.service.auth.JwtService;
import com.example.user_service.service.interfaces.UserRoleService;
import com.example.user_service.service.interfaces.UserService;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleService userRoleService;

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private MapperToUser mapperToUser;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private RegistrationRequestDto registrationRequestDto;
    private LoginRequestDto loginRequestDto;
    private User testUser;
    private final String TEST_USERNAME = "testuser";
    private final String EMAIL = "test@test.ru";
    private final String PASSWORD = "password";
    private final String ENCODED_PASSWORD = "encoded_password";
    private final String VALID_REFRESH_TOKEN = "valid-refresh-token";
    private final String NEW_ACCESS_TOKEN = "new-access-token";
    private final String NEW_REFRESH_TOKEN = "new-refresh-token";
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername(TEST_USERNAME);
        registrationRequestDto = new RegistrationRequestDto(
                TEST_USERNAME,
                PASSWORD,
                EMAIL,
                "firstname",
                "lastname",
                "phone"
        );
        loginRequestDto = new LoginRequestDto(TEST_USERNAME, PASSWORD);
    }
    @Test
    void register_Success() {
        //Given
        User userToSave = new User();
        userToSave.setUsername(TEST_USERNAME);

        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        when(mapperToUser.mapToUser(registrationRequestDto)).thenReturn(userToSave);
        when(passwordEncoder.encode(PASSWORD)).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class))).thenReturn(userToSave);

        // When
        authenticationService.register(registrationRequestDto);

        // Then
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());
        verify(userRepository).findByUsername(TEST_USERNAME);
        verify(userRepository).findByEmail(EMAIL);
        verify(mapperToUser).mapToUser(registrationRequestDto);
        verify(passwordEncoder).encode(PASSWORD);
        verify(userRoleService).addRoleForUser(userToSave, RoleType.TRADER);
        verify(eventPublisher).publishEvent(userToSave);
    }

    @Test
    void register_UsernameExists(){
        // Given
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.of(testUser));

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> authenticationService.register(registrationRequestDto));
        //Then
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Пользователь с таким username уже существует"));
        verify(userRepository, never()).save(any(User.class));
    }
    @Test
    void register_EmailExists(){
        // Given
        when(userRepository.findByUsername(TEST_USERNAME)).thenReturn(Optional.empty());
        when(userRepository.findByEmail(EMAIL)).thenReturn(Optional.of(testUser));
        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authenticationService.register(registrationRequestDto);
        });
        //Then
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Пользователь с таким email уже существует"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void authenticate_Success() {
        //Given
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                loginRequestDto.username(),
                loginRequestDto.password()
        );
        Authentication successfulAuthentication = mock(Authentication.class);

        when(authenticationManager.authenticate(authToken)).thenReturn(successfulAuthentication);
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        when(jwtService.generateRefreshToken(testUser)).thenReturn(NEW_REFRESH_TOKEN);
        when(jwtService.generateAccessToken(testUser)).thenReturn(NEW_ACCESS_TOKEN);

        //When
        AuthenticationResponseDto response = authenticationService.authenticate(loginRequestDto);
        //Then
        assertEquals(NEW_REFRESH_TOKEN, response.refreshToken());
        assertEquals(NEW_ACCESS_TOKEN, response.accessToken());
    }

    @Test
    void authenticate_BadCredentials_ThrowsException() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authenticationService.authenticate(loginRequestDto);
        });
        //Then
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void refreshToken_Success() {
        //Given
        when(jwtService.extractUserId(VALID_REFRESH_TOKEN)).thenReturn("1");
        when(userService.loadUserById(1L)).thenReturn(testUser);
        when(jwtService.isValidRefresh(VALID_REFRESH_TOKEN)).thenReturn(true);
        when(jwtService.generateAccessToken(testUser)).thenReturn(NEW_ACCESS_TOKEN);
        when(jwtService.generateRefreshToken(testUser)).thenReturn(NEW_REFRESH_TOKEN);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + VALID_REFRESH_TOKEN);

        //When
        ResponseEntity<AuthenticationResponseDto> responseEntity = authenticationService.refreshToken(request);

        //Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        AuthenticationResponseDto responseBody = responseEntity.getBody();
        assertNotNull(responseBody);
        assertEquals(NEW_ACCESS_TOKEN, responseBody.accessToken());
        assertEquals(NEW_REFRESH_TOKEN, responseBody.refreshToken());
    }

    @Test
    void refreshToken_InvalidAuthorizationHeader(){
        //Given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "WWWW" + VALID_REFRESH_TOKEN);
        //When
        ResponseEntity<AuthenticationResponseDto> responseEntity = authenticationService.refreshToken(request);
        //Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }

    @Test
    void refreshToken_InvalidToken(){
        //Given
        when(jwtService.extractUserId(VALID_REFRESH_TOKEN)).thenReturn("1");
        when(userService.loadUserById(1L)).thenReturn(testUser);
        when(jwtService.isValidRefresh(VALID_REFRESH_TOKEN)).thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + VALID_REFRESH_TOKEN);
        //When
        ResponseEntity<AuthenticationResponseDto> responseEntity = authenticationService.refreshToken(request);
        //Then
        assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());
    }
}