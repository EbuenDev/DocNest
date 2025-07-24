package com.docnest.service;

import com.docnest.dto.UserCreateRequest;
import com.docnest.dto.UserUpdateRequest;
import com.docnest.dto.UserResponse;
import com.docnest.entity.Role;
import com.docnest.entity.User;
import com.docnest.repository.RoleRepository;
import com.docnest.repository.UserRepository;
import com.docnest.repository.DepartmentRepository;
import com.docnest.entity.Department;
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
    private final DepartmentRepository departmentRepository;

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
        Department department = null;
        if (request.getDepartmentName() != null && !request.getDepartmentName().isBlank()) {
            department = departmentRepository.findByName(request.getDepartmentName())
                .orElseThrow(() -> new RuntimeException("Department not found: " + request.getDepartmentName()));
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .age(request.getAge())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .roles(roles)
                .enabled(true)
                .department(department)
                .build();
        user = userRepository.save(user);
        return toResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long userId, UserUpdateRequest request, java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> currentUserRoles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean isPrivileged = currentUserRoles.stream().anyMatch(a -> a.getAuthority().equals("ROLE_DIRECTOR") || a.getAuthority().equals("ROLE_DEAN"));
        // Only privileged users can update enabled status, department, or roles
        if (request.getEnabled() != null || request.getDepartmentName() != null || (request.getRoles() != null && !request.getRoles().isEmpty())) {
            if (!isPrivileged) {
                throw new RuntimeException("You do not have permission to update department, roles, or account status.");
            }
        }
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        if (request.getPassword() != null) user.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getEnabled() != null) user.setEnabled(request.getEnabled());
        if (request.getDepartmentName() != null) {
            Department department = departmentRepository.findByName(request.getDepartmentName())
                .orElseThrow(() -> new RuntimeException("Department not found: " + request.getDepartmentName()));
            user.setDepartment(department);
        }
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<Role> roles = request.getRoles().stream()
                .map(roleName -> roleRepository.findByName(Role.RoleName.valueOf(roleName))
                        .orElseThrow(() -> new RuntimeException("Role not found: " + roleName)))
                .collect(Collectors.toSet());
            user.setRoles(roles);
        }
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getAge() != null) user.setAge(request.getAge());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        user = userRepository.save(user);
        return toResponse(user);
    }

    @Transactional
    public void disableUser(Long userId, java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> currentUserRoles) {
        boolean isPrivileged = currentUserRoles.stream().anyMatch(a -> a.getAuthority().equals("ROLE_DIRECTOR") || a.getAuthority().equals("ROLE_DEAN"));
        if (!isPrivileged) {
            throw new RuntimeException("You do not have permission to disable users.");
        }
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

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsersWithDepartment() {
        return userRepository.findAll().stream().map(user -> {
            UserResponse resp = toResponse(user);
            resp.setDepartmentName(user.getDepartment() != null ? user.getDepartment().getName() : "");
            return resp;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserResponseById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        UserResponse resp = toResponse(user);
        resp.setDepartmentName(user.getDepartment() != null ? user.getDepartment().getName() : "");
        return resp;
    }

    @Transactional
    public User updateProfile(User user, com.docnest.dto.ProfileUpdateRequest request) {
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getAge() != null) user.setAge(request.getAge());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        return userRepository.save(user);
    }

    @Transactional
    public boolean changePassword(User user, com.docnest.dto.PasswordChangeRequest request) {
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    public UserResponse toResponse(User user) {
        UserResponse resp = new UserResponse();
        resp.setId(user.getId());
        resp.setUsername(user.getUsername());
        resp.setEmail(user.getEmail());
        resp.setEnabled(user.isEnabled());
        resp.setRoles(user.getRoles().stream().map(r -> r.getName().name()).collect(Collectors.toSet()));
        resp.setDepartmentName(user.getDepartment() != null ? user.getDepartment().getName() : "");
        resp.setFirstName(user.getFirstName());
        resp.setLastName(user.getLastName());
        resp.setAge(user.getAge());
        resp.setGender(user.getGender());
        resp.setDateOfBirth(user.getDateOfBirth());
        return resp;
    }

    public long getUserCount() {
        return userRepository.count();
    }
    public List<User> getRecentUsers(int limit) {
        return userRepository.findTop5ByOrderByIdDesc();
    }

    public List<User> getUsersByDepartment(String departmentName) {
        return userRepository.findByDepartment_Name(departmentName);
    }
} 