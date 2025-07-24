package com.docnest.service;

import org.springframework.stereotype.Service;
import com.docnest.config.FeatureProperties;

@Service
public class FeatureService {
    private final FeatureProperties featureProperties;

    public FeatureService(FeatureProperties featureProperties) {
        this.featureProperties = featureProperties;
    }

    public boolean isAccountLockoutEnabled() {
        return featureProperties.isAccountLockoutEnabled();
    }

    public boolean isDownloadLogEnabled() {
        return featureProperties.isDownloadLogEnabled();
    }

    public boolean isFileVisibilityEnabled() {
        return featureProperties.isFileVisibilityEnabled();
    }

    public boolean isFacultyApprovalEnabled() {
        try {
            java.lang.reflect.Method m = featureProperties.getClass().getMethod("isFacultyApprovalEnabled");
            return (boolean) m.invoke(featureProperties);
        } catch (Exception e) {
            // fallback for missing property
            return false;
        }
    }
} 