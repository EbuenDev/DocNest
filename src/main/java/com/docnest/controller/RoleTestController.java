package com.docnest.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class RoleTestController {

    @GetMapping("/director/secret")
    @PreAuthorize("hasRole('DIRECTOR')")
    public String directorSecret() {
        return "Only DIRECTOR can see this!";
    }

    @GetMapping("/faculty/secret")
    @PreAuthorize("hasRole('FACULTY')")
    public String facultySecret() {
        return "Only FACULTY can see this!";
    }

    @GetMapping("/guest/secret")
    @PreAuthorize("hasRole('GUEST')")
    public String guestSecret() {
        return "Only GUEST can see this!";
    }
} 