package com.studyconnect.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompleteTeacherProfileRequest {
    private Long teacherId;
    private String bio;
    private List<Long> skillIds;
}