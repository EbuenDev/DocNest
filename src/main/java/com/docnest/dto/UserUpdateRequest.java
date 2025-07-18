package com.docnest.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String email;
    private String password;
    private Boolean enabled;
} 