package com.studyconnect.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TeacherSkill {
    private String teacherEmail;
    private Skill skill;
}