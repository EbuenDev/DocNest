package com.docnest.dto;

import lombok.Data;
import java.util.Set;

@Data
public class UserCreateRequest {
    private String username;
    private String password;
    private String email;
    private Set<String> roles; // Role names to assign
    private String departmentName; // Department to assign
    private String firstName;
    private String lastName;
    private Integer age;
    private String gender;
    private java.time.LocalDate dateOfBirth;
} 