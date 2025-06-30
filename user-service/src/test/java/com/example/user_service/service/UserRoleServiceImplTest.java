package com.example.user_service.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.user_service.entity.User;
import com.example.user_service.entity.UserRole;
import com.example.user_service.enums.RoleType;
import com.example.user_service.repository.UserRoleRepository;

@ExtendWith(MockitoExtension.class)
class UserRoleServiceImplTest {

    private User testUser;
    private final String TEST_USERNAME = "testuser";
    private final String EMAIL = "test@test.ru";
    private final String PASSWORD = "password";
    private Set<UserRole> userRoles = new HashSet<>();
    @Mock
    private UserRoleRepository userRoleRepository;
    @InjectMocks
    private UserRoleServiceImpl userRoleService;

    @BeforeEach
    void setup(){
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername(TEST_USERNAME);
        UserRole role = new UserRole();
        role.setId(1L);
        role.setRoleType(RoleType.TRADER);
        role.setUser(testUser);
        userRoles.add(role);
    }

    @Test
    void checkExistUserRole_ExistedRole() {
        //Given
        when(userRoleRepository.findAllByUser(testUser)).thenReturn(userRoles);

        //When
        boolean result = userRoleService.checkExistUserRole(testUser, RoleType.TRADER);

        //Then
        assertTrue(result);
    }
    @Test
    void checkExistUserRole_NotExistedRole() {
        //Given
        when(userRoleRepository.findAllByUser(testUser)).thenReturn(userRoles);

        //When
        boolean result = userRoleService.checkExistUserRole(testUser, RoleType.ADMIN);

        //Then
        assertFalse(result);
    }

}