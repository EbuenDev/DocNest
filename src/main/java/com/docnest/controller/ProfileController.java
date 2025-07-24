package com.docnest.controller;

import com.docnest.dto.ProfileUpdateRequest;
import com.docnest.dto.PasswordChangeRequest;
import com.docnest.entity.User;
import com.docnest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;

    @Autowired
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "profile";
    }

    @PostMapping
    public String updateProfile(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
                               @RequestParam("email") String email,
                               org.springframework.ui.Model model) {
        com.docnest.entity.User user = userService.findByUsername(userDetails.getUsername());
        com.docnest.dto.ProfileUpdateRequest req = new com.docnest.dto.ProfileUpdateRequest();
        req.setEmail(email);
        userService.updateProfile(user, req);
        model.addAttribute("user", userService.findByUsername(userDetails.getUsername()));
        model.addAttribute("successMessage", "Profile updated successfully.");
        return "profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
                                 @RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 org.springframework.ui.Model model,
                                 HttpServletRequest request) {
        com.docnest.entity.User user = userService.findByUsername(userDetails.getUsername());
        com.docnest.dto.PasswordChangeRequest req = new com.docnest.dto.PasswordChangeRequest();
        req.setOldPassword(oldPassword);
        req.setNewPassword(newPassword);
        boolean success = userService.changePassword(user, req);
        if (success) {
            // Invalidate session and redirect to login
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            return "redirect:/login?passwordChanged";
        } else {
            model.addAttribute("user", userService.findByUsername(userDetails.getUsername()));
            model.addAttribute("errorMessage", "Old password is incorrect.");
            return "profile";
        }
    }
} 