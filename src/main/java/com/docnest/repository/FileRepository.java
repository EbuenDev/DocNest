package com.docnest.repository;

import com.docnest.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
 
public interface FileRepository extends JpaRepository<File, Long> {
    long count();
    List<File> findTop5ByOrderByUploadedAtDesc();
    Optional<File> findByFilename(String filename);
    List<File> findByUploadedBy_Department_Name(String departmentName);
    List<File> findByVisibility(com.docnest.entity.FileVisibility visibility);
} 