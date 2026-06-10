package com.studyconnect.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompleteTeacherProfileRequest {

    @NotBlank(message = "Bio is required")
    private String bio;

    @NotBlank(message = "Profile image is required")
    private String profileImageUrl;

    @NotEmpty(message = "At least one skill is required")
    private List<Long> skillIds;
}