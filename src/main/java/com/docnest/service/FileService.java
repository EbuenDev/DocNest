package com.docnest.service;

import com.docnest.entity.File;
import com.docnest.entity.FileVisibility;
import com.docnest.entity.User;
import com.docnest.repository.FileRepository;
import com.docnest.service.FeatureService;
import com.docnest.service.PermissionService;
import com.docnest.dto.FileResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {
    private final FileRepository fileRepository;
    private final FeatureService featureService;
    private final PermissionService permissionService;

    private final Path uploadDir;

    public FileService(FileRepository fileRepository, FeatureService featureService,
                      @Value("${file.upload-dir:uploads}") String uploadDir,
                      PermissionService permissionService) {
        this.fileRepository = fileRepository;
        this.featureService = featureService;
        this.permissionService = permissionService;
        this.uploadDir = Paths.get(uploadDir);
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public File uploadFile(MultipartFile file, String title, FileVisibility visibility, User uploader) throws IOException {
        // Validate PDF
        if (!file.getContentType().equalsIgnoreCase("application/pdf")) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }
        // Save file to disk
        String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), filePath);

        // Save metadata
        File dbFile = new File();
        dbFile.setTitle(title);
        dbFile.setFilename(filename);
        dbFile.setFilePath(filePath.toString());
        dbFile.setFileSize(file.getSize());
        dbFile.setMimeType(file.getContentType());
        dbFile.setVisibility(featureService.isFileVisibilityEnabled() ? visibility : FileVisibility.PUBLIC);
        dbFile.setUploadedBy(uploader);
        dbFile.setUploadedAt(LocalDateTime.now());
        boolean isDirector = uploader.getRoles().stream()
            .anyMatch(r -> r.getName().name().equals("DIRECTOR"));
        dbFile.setStatus(isDirector ? "APPROVED" : "PENDING");
        return fileRepository.save(dbFile);
    }

    public List<File> listFilesForUser(User user) {
        if (permissionService.canApprove(user)) {
            // Approvers see all files
            return fileRepository.findAll();
        } else if (user.getRoles().stream().anyMatch(r -> r.getName().name().equals("FACULTY"))) {
            // Faculty see their uploads and public files
            return fileRepository.findAll().stream()
                    .filter(f -> f.getVisibility() == FileVisibility.PUBLIC ||
                                 (f.getUploadedBy() != null && f.getUploadedBy().getId().equals(user.getId())))
                    .collect(Collectors.toList());
        } else {
            // Guest
            return fileRepository.findAll().stream()
                    .filter(f -> f.getVisibility() == FileVisibility.PUBLIC)
                    .collect(Collectors.toList());
        }
    }

    public File getFileForDownload(Long fileId, User user) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        // Enforce visibility
        if (featureService.isFileVisibilityEnabled()) {
            if (file.getVisibility() == FileVisibility.FACULTY_ONLY) {
                boolean isDirector = user.getRoles().stream().anyMatch(r -> r.getName().name().equals("DIRECTOR"));
                boolean isFaculty = user.getRoles().stream().anyMatch(r -> r.getName().name().equals("FACULTY"));
                boolean isUploader = file.getUploadedBy() != null && file.getUploadedBy().getId().equals(user.getId());
                if (!(isDirector || isFaculty || isUploader)) {
                    throw new RuntimeException("You do not have access to this file");
                }
            }
        }
        return file;
    }

    public File findById(Long id) {
        return fileRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("File not found with id: " + id));
    }

    public File save(File file) {
        return fileRepository.save(file);
    }

    public FileResponse toFileResponse(File file) {
        FileResponse dto = new FileResponse();
        dto.setId(file.getId());
        dto.setTitle(file.getTitle());
        dto.setFilename(file.getFilename());
        dto.setStatus(file.getStatus());
        dto.setVisibility(file.getVisibility().name());
        dto.setUploadedBy(file.getUploadedBy() != null ? file.getUploadedBy().getUsername() : null);
        return dto;
    }

    public long getFileCount() {
        return fileRepository.count();
    }
    public List<com.docnest.entity.File> getRecentFiles(int limit) {
        return fileRepository.findTop5ByOrderByUploadedAtDesc();
    }

    public List<com.docnest.entity.File> getFilesByDepartment(String departmentName) {
        return fileRepository.findByUploadedBy_Department_Name(departmentName);
    }

    public List<com.docnest.entity.File> getPublicFiles() {
        return fileRepository.findByVisibility(com.docnest.entity.FileVisibility.PUBLIC);
    }
} 