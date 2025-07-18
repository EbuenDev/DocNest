package com.docnest.service;

import com.docnest.dto.UserCreateRequest;
import com.docnest.dto.UserUpdateRequest;
import com.docnest.dto.UserResponse;
import com.docnest.entity.Role;
import com.docnest.entity.User;
import com.docnest.repository.RoleRepository;
import com.docnest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserCreateRequest request, String creatorRole) {
        // Validation: Faculty cannot create Director
        if (creatorRole.equals("FACULTY") && request.getRoles().contains("DIRECTOR")) {
            throw new RuntimeException("Faculty cannot create Director users");
        }
        Set<Role> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(Role.RoleName.valueOf(roleName))
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .roles(roles)
                .enabled(true)
                .build();
        user = userRepository.save(user);
        return toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getEnabled() != null) user.setEnabled(request.getEnabled());
        user = userRepository.save(user);
        return toResponse(user);
    }

    @Transactional
    public void disableUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(String roleName) {
        Role role = roleRepository.findByName(Role.RoleName.valueOf(roleName))
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));
        List<User> users = userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains(role))
                .collect(Collectors.toList());
        return users.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private UserResponse toResponse(User user) {
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setEmail(user.getEmail());
        resp.setEnabled(user.isEnabled());
        resp.setRoles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()));
        return resp;
    }
} 