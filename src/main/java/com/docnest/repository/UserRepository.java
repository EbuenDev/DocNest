package com.docnest.repository;

import com.docnest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    long count();
    List<User> findTop5ByOrderByIdDesc();
    List<User> findByDepartment_Name(String departmentName);
} 