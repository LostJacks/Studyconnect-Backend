package com.studyconnect.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TeacherResponse {
    private Long id;
    private String name;
    private String bio;
    private List<SkillResponse> skills;
}