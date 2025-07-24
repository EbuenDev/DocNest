package com.docnest.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
    private boolean enabled;
    private String departmentName;
    private String firstName;
    private String lastName;
    private Integer age;
    private String gender;
    private java.time.LocalDate dateOfBirth;
} 