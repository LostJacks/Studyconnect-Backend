package com.studyconnect.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public class UpdateProfileImageRequest {

    @NotBlank
    private String profileImageUrl;

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}