package com.docnest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "feature")
public class FeatureProperties {
    private boolean accountLockoutEnabled = true;
    private boolean downloadLogEnabled = true;
    private boolean fileVisibilityEnabled = true;
    private boolean facultyApprovalEnabled = false;

    public boolean isAccountLockoutEnabled() {
        return accountLockoutEnabled;
    }
    public void setAccountLockoutEnabled(boolean accountLockoutEnabled) {
        this.accountLockoutEnabled = accountLockoutEnabled;
    }
    public boolean isDownloadLogEnabled() {
        return downloadLogEnabled;
    }
    public void setDownloadLogEnabled(boolean downloadLogEnabled) {
        this.downloadLogEnabled = downloadLogEnabled;
    }
    public boolean isFileVisibilityEnabled() {
        return fileVisibilityEnabled;
    }
    public void setFileVisibilityEnabled(boolean fileVisibilityEnabled) {
        this.fileVisibilityEnabled = fileVisibilityEnabled;
    }
    public boolean isFacultyApprovalEnabled() {
        return facultyApprovalEnabled;
    }
    public void setFacultyApprovalEnabled(boolean facultyApprovalEnabled) {
        this.facultyApprovalEnabled = facultyApprovalEnabled;
    }
} 