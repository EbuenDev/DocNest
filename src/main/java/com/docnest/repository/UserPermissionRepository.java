package com.docnest.repository;

import com.docnest.entity.UserPermission;
import com.docnest.entity.Permission;
import com.docnest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    boolean existsByUserAndPermission(User user, Permission permission);
} 