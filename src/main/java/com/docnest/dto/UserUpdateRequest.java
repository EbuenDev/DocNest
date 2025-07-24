package com.docnest.dto;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String email;
    private String password;
    private Boolean enabled;
    private String firstName;
    private String lastName;
    private Integer age;
    private String gender;
    private java.time.LocalDate dateOfBirth;
    private String departmentName;
    private java.util.Set<String> roles;
} 