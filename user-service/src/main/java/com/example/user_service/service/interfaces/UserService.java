package com.example.user_service.service.interfaces;

import com.example.user_service.entity.User;

public interface UserService {
    User loadUserByUsername(String username);
}
