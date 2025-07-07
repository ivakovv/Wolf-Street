package com.example.user_service.service;

import com.example.user_service.entity.User;
import com.example.user_service.entity.UserRole;
import com.example.user_service.enums.RoleType;
import com.example.user_service.repository.UserRoleRepository;
import com.example.user_service.service.interfaces.UserRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository userRoleRepository;

    public List<String> getAllUserRoles(User user){
        return userRoleRepository.findAllByUser(user)
                .stream()
                .map(userRole -> userRole.getRoleType().name())
                .collect(Collectors.toList());
    }

    public void addRoleForUser(User user, RoleType roleType){
        if(!checkExistUserRole(user, roleType)){
            UserRole userRole = UserRole.builder()
                    .user(user)
                    .roleType(roleType)
                    .build();
            userRoleRepository.save(userRole);
        }
    }

    public void removeUserRole(User user, RoleType roleType){
        userRoleRepository.findAllByUser(user)
                .stream()
                .filter(userRole -> userRole.getRoleType().equals(roleType))
                .findFirst()
                .ifPresent(userRoleRepository::delete);
    }

    public boolean checkExistUserRole(User user, RoleType roleType){
        Set<UserRole> allUserRoles = userRoleRepository.findAllByUser(user);
        return allUserRoles.stream().anyMatch(ur -> ur.getRoleType().equals(roleType));
    }
}
