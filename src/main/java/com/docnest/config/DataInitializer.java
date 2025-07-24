package com.docnest.config;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.docnest.entity.Department;
import com.docnest.entity.Role;
import com.docnest.entity.User;
import com.docnest.repository.DepartmentRepository;
import com.docnest.repository.RoleRepository;
import com.docnest.repository.UserRepository;
import com.docnest.repository.FileRepository;
import com.docnest.entity.File;
import com.docnest.entity.FileVisibility;
import com.docnest.service.NotificationService;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final FileRepository fileRepository;
    private final NotificationService notificationService;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Create roles if not exist
            for (Role.RoleName roleName : Role.RoleName.values()) {
                roleRepository.findByName(roleName)
                        .orElseGet(() -> roleRepository.save(new Role(null, roleName)));
            }

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // Create departments if not exist
            //Department itDept = departmentRepository.findByName("IT").orElseGet(() -> departmentRepository.save(new Department(null, "IT")));
            Department itDept = departmentRepository.findByName("IT")
                .orElseGet(() -> {
                    Department d = new Department();
                    d.setName("IT");
                    return departmentRepository.save(d);
                });
            //Department businessDept = departmentRepository.findByName("Business Administration").orElseGet(() -> departmentRepository.save(new Department(null, "Business Administration")));
            Department businessDept = departmentRepository.findByName("Business Administration")
                .orElseGet(() -> {
                    Department d = new Department();
                    d.setName("Business Administration");
                    return departmentRepository.save(d);
                });
            //Department educationDept = departmentRepository.findByName("Education").orElseGet(() -> departmentRepository.save(new Department(null, "Education")));
            Department educationDept = departmentRepository.findByName("Education Department")
                .orElseGet(() -> {
                    Department d = new Department();
                    d.setName("Education Department");
                    return departmentRepository.save(d);
                });

            // Create test users if not exist
            if (userRepository.findByUsername("jdoe-0001").isEmpty()) {
                userRepository.save(User.builder()
                        .username("jdoe-0001")
                        .email("jdoe@campus.edu")
                        .password(encoder.encode("password123"))
                        .roles(Set.of(roleRepository.findByName(Role.RoleName.DIRECTOR).get()))
                        .department(itDept)
                        .firstName("John")
                        .lastName("Doe")
                        .age(45)
                        .gender("Male")
                        .dateOfBirth(java.time.LocalDate.of(1980, 1, 1))
                        .build());
            }
            if (userRepository.findByUsername("asmith-0002").isEmpty()) {
                userRepository.save(User.builder()
                        .username("asmith-0002")
                        .email("asmith@campus.edu")
                        .password(encoder.encode("password123"))
                        .roles(Set.of(roleRepository.findByName(Role.RoleName.FACULTY).get()))
                        .department(businessDept)
                        .firstName("Alice")
                        .lastName("Smith")
                        .age(38)
                        .gender("Female")
                        .dateOfBirth(java.time.LocalDate.of(1987, 5, 15))
                        .build());
            }
            if (userRepository.findByUsername("blee-0003").isEmpty()) {
                userRepository.save(User.builder()
                        .username("blee-0003")
                        .email("blee@campus.edu")
                        .password(encoder.encode("password123"))
                        .roles(Set.of(roleRepository.findByName(Role.RoleName.GUEST).get()))
                        .department(educationDept)
                        .firstName("Brian")
                        .lastName("Lee")
                        .age(22)
                        .gender("Male")
                        .dateOfBirth(java.time.LocalDate.of(2003, 9, 10))
                        .build());
            }
            if (userRepository.findByUsername("mjohnson-0004").isEmpty()) {
                userRepository.save(User.builder()
                        .username("mjohnson-0004")
                        .email("mjohnson@campus.edu")
                        .password(encoder.encode("password123"))
                        .roles(Set.of(roleRepository.findByName(Role.RoleName.DEAN).get()))
                        .department(itDept)
                        .firstName("Mary")
                        .lastName("Johnson")
                        .age(50)
                        .gender("Female")
                        .dateOfBirth(java.time.LocalDate.of(1975, 3, 20))
                        .build());
            }
            if (userRepository.findByUsername("ssmith-0005").isEmpty()) {
                userRepository.save(User.builder()
                        .username("ssmith-0005")
                        .email("ssmith@campus.edu")
                        .password(encoder.encode("password123"))
                        .roles(Set.of(roleRepository.findByName(Role.RoleName.DEAN).get()))
                        .department(businessDept)
                        .firstName("Samuel")
                        .lastName("Smith")
                        .age(48)
                        .gender("Male")
                        .dateOfBirth(java.time.LocalDate.of(1977, 7, 8))
                        .build());
            }
            // Additional users for department testing
            if (userRepository.findByUsername("jcarter-0006").isEmpty()) {
                userRepository.save(User.builder()
                        .username("jcarter-0006")
                        .email("jcarter@campus.edu")
                        .password(encoder.encode("password123"))
                        .roles(Set.of(roleRepository.findByName(Role.RoleName.FACULTY).get()))
                        .department(itDept)
                        .firstName("James")
                        .lastName("Carter")
                        .age(35)
                        .gender("Male")
                        .dateOfBirth(java.time.LocalDate.of(1989, 2, 14))
                        .build());
            }
            if (userRepository.findByUsername("lwilson-0007").isEmpty()) {
                userRepository.save(User.builder()
                        .username("lwilson-0007")
                        .email("lwilson@campus.edu")
                        .password(encoder.encode("password123"))
                        .roles(Set.of(roleRepository.findByName(Role.RoleName.FACULTY).get()))
                        .department(businessDept)
                        .firstName("Linda")
                        .lastName("Wilson")
                        .age(41)
                        .gender("Female")
                        .dateOfBirth(java.time.LocalDate.of(1983, 11, 5))
                        .build());
            }
            if (userRepository.findByUsername("tnguyen-0008").isEmpty()) {
                userRepository.save(User.builder()
                        .username("tnguyen-0008")
                        .email("tnguyen@campus.edu")
                        .password(encoder.encode("password123"))
                        .roles(Set.of(roleRepository.findByName(Role.RoleName.FACULTY).get()))
                        .department(educationDept)
                        .firstName("Tom")
                        .lastName("Nguyen")
                        .age(29)
                        .gender("Male")
                        .dateOfBirth(java.time.LocalDate.of(1995, 6, 30))
                        .build());
            }
            // Seed files for each department
            com.docnest.entity.File sampleFile1 = new com.docnest.entity.File();
            sampleFile1.setTitle("IT Department Handbook");
            sampleFile1.setFilename("it-handbook.pdf");
            sampleFile1.setFilePath("/files/it-handbook.pdf");
            sampleFile1.setFileSize(102400L);
            sampleFile1.setMimeType("application/pdf");
            sampleFile1.setVisibility(com.docnest.entity.FileVisibility.FACULTY_ONLY);
            sampleFile1.setUploadedBy(userRepository.findByUsername("jcarter-0006").get());
            sampleFile1.setUploadedAt(java.time.LocalDateTime.now().minusDays(2));
            sampleFile1.setStatus("APPROVED");
            if (!fileExists("it-handbook.pdf")) fileRepository.save(sampleFile1);

            com.docnest.entity.File sampleFile2 = new com.docnest.entity.File();
            sampleFile2.setTitle("Business Admin Syllabus");
            sampleFile2.setFilename("ba-syllabus.pdf");
            sampleFile2.setFilePath("/files/ba-syllabus.pdf");
            sampleFile2.setFileSize(204800L);
            sampleFile2.setMimeType("application/pdf");
            sampleFile2.setVisibility(com.docnest.entity.FileVisibility.FACULTY_ONLY);
            sampleFile2.setUploadedBy(userRepository.findByUsername("lwilson-0007").get());
            sampleFile2.setUploadedAt(java.time.LocalDateTime.now().minusDays(1));
            sampleFile2.setStatus("APPROVED");
            if (!fileExists("ba-syllabus.pdf")) fileRepository.save(sampleFile2);

            com.docnest.entity.File sampleFile3 = new com.docnest.entity.File();
            sampleFile3.setTitle("Education Dept Policy");
            sampleFile3.setFilename("edu-policy.pdf");
            sampleFile3.setFilePath("/files/edu-policy.pdf");
            sampleFile3.setFileSize(51200L);
            sampleFile3.setMimeType("application/pdf");
            sampleFile3.setVisibility(com.docnest.entity.FileVisibility.FACULTY_ONLY);
            sampleFile3.setUploadedBy(userRepository.findByUsername("tnguyen-0008").get());
            sampleFile3.setUploadedAt(java.time.LocalDateTime.now().minusHours(10));
            sampleFile3.setStatus("APPROVED");
            if (!fileExists("edu-policy.pdf")) fileRepository.save(sampleFile3);

            // Seed notifications for test users
            User jdoe = userRepository.findByUsername("jdoe-0001").orElse(null);
            User asmith = userRepository.findByUsername("asmith-0002").orElse(null);
            User blee = userRepository.findByUsername("blee-0003").orElse(null);
            User mjohnson = userRepository.findByUsername("mjohnson-0004").orElse(null);
            User ssmith = userRepository.findByUsername("ssmith-0005").orElse(null);
            if (jdoe != null) notificationService.createNotification(jdoe, "Welcome to DocNest, Director John!", "INFO");
            if (asmith != null) notificationService.createNotification(asmith, "Your file 'ba-syllabus.pdf' was approved.", "FILE_APPROVED");
            if (blee != null) notificationService.createNotification(blee, "A new file 'edu-policy.pdf' is available for you.", "NEW_FILE");
            if (mjohnson != null) notificationService.createNotification(mjohnson, "You have 2 files pending approval.", "ALERT");
            if (ssmith != null) notificationService.createNotification(ssmith, "Welcome to the Business Admin department!", "INFO");

            // Helper to avoid duplicate files
            // ... existing code ...
        };
    }

    private boolean fileExists(String filename) {
        return fileRepository.findByFilename(filename).isPresent();
    }
} 