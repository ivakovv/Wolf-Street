package com.example.user_service.service.interfaces;


import com.example.user_service.entity.User;
import com.example.user_service.enums.RoleType;

import java.util.List;

public interface UserRoleService {
    List<String> getAllUserRoles(User user);
    void addRoleForUser(User user, RoleType roleType);
    void removeUserRole(User user, RoleType roleType);
}
