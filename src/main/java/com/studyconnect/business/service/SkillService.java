package com.studyconnect.business.service;

import com.studyconnect.data.irepository.ISkillRepository;
import com.studyconnect.data.irepository.ITeacherSkillRepository;
import com.studyconnect.domain.model.Skill;
import com.studyconnect.domain.model.TeacherSkill;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    private final ISkillRepository skillRepository;
    private final ITeacherSkillRepository teacherSkillRepository;

    public SkillService(ISkillRepository skillRepository,
                        ITeacherSkillRepository teacherSkillRepository) {
        this.skillRepository = skillRepository;
        this.teacherSkillRepository = teacherSkillRepository;
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public TeacherSkill addSkillToTeacher(Long teacherId, Long skillId) {

        if (teacherId == null || skillId == null) {
            throw new IllegalArgumentException("TeacherId and SkillId must be provided");
        }

        Skill skill = skillRepository.findById(skillId);

        if (skill == null) {
            throw new IllegalArgumentException("Skill does not exist");
        }

        TeacherSkill teacherSkill = new TeacherSkill(teacherId, skillId);

        return teacherSkillRepository.save(teacherSkill);
    }

    public List<Skill> getSkillsByTeacher(Long teacherId) {

        if (teacherId == null) {
            throw new IllegalArgumentException("TeacherId cannot be null");
        }

        return teacherSkillRepository.findSkillsByTeacherId(teacherId);
    }
}