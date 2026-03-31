package com.studyconnect.data.irepository;

import com.studyconnect.domain.model.Skill;
import java.util.List;

public interface ITeacherSkillRepository {
    void saveByIds(Long teacherId, Long skillId);
    List<Skill> findSkillsByTeacherId(Long teacherId);
}