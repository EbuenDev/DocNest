package com.docnest.service;

import com.docnest.entity.Permission;
import com.docnest.entity.User;
import com.docnest.repository.UserPermissionRepository;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {
    private final FeatureService featureService;
    private final UserPermissionRepository userPermissionRepository;

    public PermissionService(FeatureService featureService, UserPermissionRepository userPermissionRepository) {
        this.featureService = featureService;
        this.userPermissionRepository = userPermissionRepository;
    }

    public boolean canApprove(User user) {
        boolean isDirector = user.getRoles().stream()
            .anyMatch(r -> r.getName().name().equals("DIRECTOR"));
        if (isDirector) return true;
        boolean isFaculty = user.getRoles().stream()
            .anyMatch(r -> r.getName().name().equals("FACULTY"));
        if (isFaculty && featureService.isFacultyApprovalEnabled()) {
            return true;
        }
        // Check for user-specific permission
        if (userPermissionRepository.existsByUserAndPermission(user, Permission.APPROVE_DOCUMENT)) {
            return true;
        }
        return false;
    }
} 