package com.studyconnect.data.irepository;

import com.studyconnect.domain.model.Skill;
import com.studyconnect.domain.model.TeacherSkill;
import java.util.List;

public interface ITeacherSkillRepository {
    TeacherSkill save(TeacherSkill teacherSkill);
    List<Skill> findSkillsByTeacherId(Long teacherId);
}