package com.docnest.controller;

import com.docnest.entity.Notification;
import com.docnest.entity.User;
import com.docnest.service.NotificationService;
import com.docnest.service.UserService;
import com.docnest.dto.NotificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    private final UserService userService;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    @ResponseBody
    @RequestMapping(value = "/api", method = RequestMethod.GET)
    public List<NotificationResponse> getNotificationsApi(@AuthenticationPrincipal UserDetails userDetails, Principal principal) {
        String username = null;
        if (userDetails != null) {
            username = userDetails.getUsername();
        } else if (principal != null) {
            username = principal.getName();
        }
        System.out.println("[DEBUG] Fetching notifications for username: " + username);
        if (username == null) {
            // Not authenticated, return empty list or throw 401
            return List.of();
        }
        User user = userService.findByUsername(username);
        return notificationService.getNotificationsForUser(user)
                .stream()
                .map(notificationService::toResponse)
                .toList();
    }

    @GetMapping("")
    public String showNotificationsPage(@AuthenticationPrincipal UserDetails userDetails, Principal principal, Model model) {
        String username = null;
        if (userDetails != null) {
            username = userDetails.getUsername();
        } else if (principal != null) {
            username = principal.getName();
        }
        if (username == null) {
            return "redirect:/login";
        }
        User user = userService.findByUsername(username);
        List<NotificationResponse> notifications = notificationService.getNotificationsForUser(user)
                .stream()
                .map(notificationService::toResponse)
                .toList();
        model.addAttribute("notifications", notifications);
        return "notifications";
    }

    @PostMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        Notification notification = notificationService.getNotificationsForUser(user).stream()
                .filter(n -> n.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notificationService.markAsRead(notification);
    }
} 