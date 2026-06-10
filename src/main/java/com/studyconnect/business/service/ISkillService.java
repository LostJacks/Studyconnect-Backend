package com.studyconnect.business.service;

import com.studyconnect.domain.model.Skill;
import com.studyconnect.presentation.dto.SkillResponse;

import java.util.List;

public interface ISkillService {
    List<Skill> getAllSkills();
    List<SkillResponse> getAllSkillResponses();
    void addSkillToTeacher(Long teacherId, Long skillId);
    List<Skill> getSkillsByTeacher(Long teacherId);
}