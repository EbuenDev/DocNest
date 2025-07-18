package com.docnest.controller;

import com.docnest.dto.UserCreateRequest;
import com.docnest.dto.UserUpdateRequest;
import com.docnest.dto.UserResponse;
import com.docnest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // Create user (Director or Faculty)
    @PostMapping
    @PreAuthorize("hasAnyRole('DIRECTOR', 'FACULTY')")
    public UserResponse createUser(@RequestBody UserCreateRequest request, Authentication authentication) {
        String creatorRole = authentication.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .filter(r -> r.equals("DIRECTOR") || r.equals("FACULTY"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No valid role found"));
        return userService.createUser(request, creatorRole);
    }

    // Update user profile
    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('DIRECTOR', 'FACULTY') or #userId == principal.id")
    public UserResponse updateUser(@PathVariable Long userId, @RequestBody UserUpdateRequest request) {
        return userService.updateUser(userId, request);
    }

    // Disable user (soft delete)
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('DIRECTOR', 'FACULTY')")
    public void disableUser(@PathVariable Long userId) {
        userService.disableUser(userId);
    }

    // Get users by role
    @GetMapping("/role/{roleName}")
    @PreAuthorize("hasAnyRole('DIRECTOR', 'FACULTY')")
    public List<UserResponse> getUsersByRole(@PathVariable String roleName) {
        return userService.getUsersByRole(roleName);
    }
} 