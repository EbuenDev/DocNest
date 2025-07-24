package com.docnest.controller;

import com.docnest.entity.File;
import com.docnest.entity.FileVisibility;
import com.docnest.entity.User;
import com.docnest.service.FileService;
import com.docnest.service.UserService;
import com.docnest.service.PermissionService;
import com.docnest.dto.FileResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/files")
public class FileController {
    private final FileService fileService;
    private final UserService userService;
    private final PermissionService permissionService;

    @Autowired
    public FileController(FileService fileService, UserService userService, PermissionService permissionService) {
        this.fileService = fileService;
        this.userService = userService;
        this.permissionService = permissionService;
    }

    @PreAuthorize("hasRole('DIRECTOR') or hasRole('FACULTY')")
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("title") String title,
                                        @RequestParam(value = "visibility", defaultValue = "FACULTY_ONLY") FileVisibility visibility,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            File saved = fileService.uploadFile(file, title, visibility, user);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    @GetMapping
    public ResponseEntity<List<FileResponse>> listFiles(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<File> files = fileService.listFilesForUser(user);
        List<FileResponse> response = files.stream()
            .map(fileService::toFileResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable Long id,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            File file = fileService.getFileForDownload(id, user);
            Path path = Path.of(file.getFilePath());
            byte[] data = Files.readAllBytes(path);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(data);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File download failed");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<?> approveFile(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        if (!permissionService.canApprove(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to approve files.");
        }
        File file = fileService.findById(id);
        file.setStatus("APPROVED");
        // Optionally: file.setApprovedBy(user); file.setApprovedAt(LocalDateTime.now());
        fileService.save(file);
        return ResponseEntity.ok("File approved.");
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<?> rejectFile(@PathVariable Long id, @RequestParam(value = "reason", required = false) String reason, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        if (!permissionService.canApprove(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You do not have permission to reject files.");
        }
        File file = fileService.findById(id);
        file.setStatus("REJECTED");
        // Optionally: file.setRejectedBy(user); file.setRejectedAt(LocalDateTime.now()); file.setRejectionReason(reason);
        fileService.save(file);
        return ResponseEntity.ok("File rejected.");
    }
} 