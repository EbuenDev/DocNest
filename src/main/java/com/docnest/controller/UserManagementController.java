package com.docnest.controller;

import com.docnest.dto.UserResponse;
import com.docnest.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.docnest.dto.UserCreateRequest;
import com.docnest.dto.UserUpdateRequest;
import com.docnest.entity.Department;
import com.docnest.entity.Role;
import com.docnest.repository.DepartmentRepository;
import com.docnest.repository.RoleRepository;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.stream.Collectors;

import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

@Controller
@RequiredArgsConstructor
public class UserManagementController {
    private final UserService userService;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository roleRepository;

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<UserResponse> users = userService.getAllUsersWithDepartment();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/users/add")
    public String showAddUserForm(Model model, @AuthenticationPrincipal UserDetails currentUser) {
        UserCreateRequest userForm = new UserCreateRequest();
        // Set default values if needed
        model.addAttribute("userForm", userForm);
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("roles",
            roleRepository.findAll().stream().map(r -> r.getName().name()).collect(Collectors.toList()));
        model.addAttribute("formTitle", "Add User");
        model.addAttribute("currentUserRoles", currentUser.getAuthorities());
        return "user-form";
    }

    @GetMapping("/users/edit/{userId}")
    public String showEditUserForm(@PathVariable Long userId, Model model, @AuthenticationPrincipal UserDetails currentUser) {
        boolean isPrivileged = currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DIRECTOR") || a.getAuthority().equals("ROLE_DEAN"));
        if (!isPrivileged) {
            model.addAttribute("errorMessage", "You do not have permission to edit users.");
            return "users";
        }
        UserResponse user = userService.getUserResponseById(userId);
        UserCreateRequest userForm = new UserCreateRequest();
        userForm.setUsername(user.getUsername());
        userForm.setEmail(user.getEmail());
        userForm.setRoles(user.getRoles());
        userForm.setDepartmentName(user.getDepartmentName());
        userForm.setFirstName(user.getFirstName());
        userForm.setLastName(user.getLastName());
        userForm.setAge(user.getAge());
        userForm.setGender(user.getGender());
        userForm.setDateOfBirth(user.getDateOfBirth());
        model.addAttribute("userForm", userForm);
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("roles",
            roleRepository.findAll().stream().map(r -> r.getName().name()).collect(Collectors.toList()));
        model.addAttribute("formTitle", "Edit User");
        model.addAttribute("editMode", true);
        model.addAttribute("userId", userId);
        model.addAttribute("currentUserRoles", currentUser.getAuthorities());
        return "user-form";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute("userForm") UserCreateRequest userForm, @RequestParam(value = "userId", required = false) Long userId, @AuthenticationPrincipal UserDetails currentUser, Model model) {
        try {
            if (userId != null) {
                // Edit mode
                UserUpdateRequest updateRequest = new UserUpdateRequest();
                updateRequest.setEmail(userForm.getEmail());
                updateRequest.setPassword(userForm.getPassword());
                updateRequest.setEnabled(true); // or keep as is
                // Optionally update roles and department if needed
                userService.updateUser(userId, updateRequest, currentUser.getAuthorities());
            } else {
                // Add mode
                userService.createUser(userForm, "DIRECTOR"); // For now, treat as Director-created
            }
            return "redirect:/users";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("users", userService.getAllUsersWithDepartment());
            return "users";
        }
    }

    @GetMapping("/users/disable/{userId}")
    public String disableUser(@PathVariable Long userId, Model model, @AuthenticationPrincipal UserDetails currentUser) {
        try {
            userService.disableUser(userId, currentUser.getAuthorities());
            return "redirect:/users";
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("users", userService.getAllUsersWithDepartment());
            return "users";
        }
    }
} 