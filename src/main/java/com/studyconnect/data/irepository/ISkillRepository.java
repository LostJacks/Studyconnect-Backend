package com.studyconnect.data.irepository;

import com.studyconnect.domain.model.Skill;
import com.studyconnect.presentation.dto.SkillResponse;

import java.util.List;

public interface ISkillRepository {
    List<Skill> findAll();
    Skill findById(Long skillId);
    List<String> findSkillNamesByTeacherId(Long teacherId);
    List<SkillResponse> findSkillsByTeacherId(Long teacherId);
    void deleteTeacherSkillsByTeacherId(Long teacherId);
    void addTeacherSkill(Long teacherId, Long skillId);
    List<SkillResponse> findAllSkillResponses();
}