package com.docnest.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserCreateRequest {
    private String username;
    private String password;
    private String email;
    private Set<String> roles; // Role names to assign
} 