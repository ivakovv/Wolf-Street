package com.example.user_service.repository;

import com.example.user_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.user_service.entity.UserRole;

import java.util.Set;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Set<UserRole> findAllByUser(User user);
}
