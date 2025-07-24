package com.docnest.controller;

import com.docnest.entity.User;
import com.docnest.service.UserService;
import com.docnest.service.FileService;
import com.docnest.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {
    private final UserService userService;
    private final FileService fileService;
    private final DepartmentService departmentService;

    @Autowired
    public DashboardController(UserService userService, FileService fileService, DepartmentService departmentService) {
        this.userService = userService;
        this.fileService = fileService;
        this.departmentService = departmentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("user", userService.toResponse(user));
        String userRole = "GUEST";
        if (user.getRoles().stream().anyMatch(r -> r.getName().name().equals("DIRECTOR"))) {
            userRole = "DIRECTOR";
            model.addAttribute("userCount", userService.getUserCount());
            model.addAttribute("fileCount", fileService.getFileCount());
            model.addAttribute("departmentCount", departmentService.getDepartmentCount());
            model.addAttribute("recentUsers", userService.getRecentUsers(5));
            model.addAttribute("recentFiles", fileService.getRecentFiles(5));
        } else if (user.getRoles().stream().anyMatch(r -> r.getName().name().equals("DEAN"))) {
            userRole = "DEAN";
            String dept = user.getDepartment().getName();
            List<User> deptUsers = userService.getUsersByDepartment(dept);
            List<com.docnest.entity.File> deptFiles = fileService.getFilesByDepartment(dept);
            model.addAttribute("deptUserCount", deptUsers.size());
            model.addAttribute("deptFileCount", deptFiles.size());
            model.addAttribute("deptRecentUsers", deptUsers.stream().limit(5).toList());
            model.addAttribute("deptRecentFiles", deptFiles.stream().limit(5).toList());
        } else if (user.getRoles().stream().anyMatch(r -> r.getName().name().equals("FACULTY"))) {
            userRole = "FACULTY";
            String dept = user.getDepartment().getName();
            List<User> deptUsers = userService.getUsersByDepartment(dept);
            List<com.docnest.entity.File> deptFiles = fileService.getFilesByDepartment(dept);
            model.addAttribute("deptUserCount", deptUsers.size());
            model.addAttribute("deptFileCount", deptFiles.size());
            model.addAttribute("deptRecentFiles", deptFiles.stream().limit(5).toList());
        } else if (user.getRoles().stream().anyMatch(r -> r.getName().name().equals("GUEST"))) {
            userRole = "GUEST";
            model.addAttribute("publicRecentFiles", fileService.getPublicFiles().stream().limit(5).toList());
        }
        model.addAttribute("userRole", userRole);
        return "dashboard-layout";
    }
} 