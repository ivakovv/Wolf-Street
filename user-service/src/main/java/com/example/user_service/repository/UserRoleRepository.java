package com.example.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.user_service.entity.UserRole;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
}
