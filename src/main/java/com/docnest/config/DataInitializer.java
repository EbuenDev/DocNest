package com.docnest.config;

import com.docnest.entity.Role;
import com.docnest.entity.User;
import com.docnest.repository.RoleRepository;
import com.docnest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Create roles if not exist
            for (Role.RoleName roleName : Role.RoleName.values()) {
                roleRepository.findByName(roleName)
                        .orElseGet(() -> roleRepository.save(new Role(null, roleName)));
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // Create test users if not exist
            if (userRepository.findByUsername("director").isEmpty()) {
                userRepository.save(User.builder()
                        .username("director")
                        .email("director@campus.edu")
                        .password(encoder.encode("password123"))
                        .roles(Set.of(roleRepository.findByName(Role.RoleName.DIRECTOR).get()))
                        .build());
            }
            if (userRepository.findByUsername("faculty").isEmpty()) {
                userRepository.save(User.builder()
                        .username("faculty")
                        .email("faculty@campus.edu")
                        .password(encoder.encode("password123"))
                        .roles(Set.of(roleRepository.findByName(Role.RoleName.FACULTY).get()))
                        .build());
            }
            if (userRepository.findByUsername("guest").isEmpty()) {
                userRepository.save(User.builder()
                        .username("guest")
                        .email("guest@campus.edu")
                        .password(encoder.encode("password123"))
                        .roles(Set.of(roleRepository.findByName(Role.RoleName.GUEST).get()))
                        .build());
            }
        };
    }
} 