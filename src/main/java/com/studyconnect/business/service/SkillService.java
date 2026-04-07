package com.studyconnect.business.service;

import com.studyconnect.data.irepository.ISkillRepository;
import com.studyconnect.data.irepository.ITeacherSkillRepository;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.domain.model.Role;
import com.studyconnect.domain.model.Skill;
import com.studyconnect.domain.model.User;
import com.studyconnect.presentation.dto.SkillResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SkillService {

    private final ISkillRepository skillRepository;
    private final ITeacherSkillRepository teacherSkillRepository;
    private final IUserRepository userRepository;

    public SkillService(ISkillRepository skillRepository,
                        ITeacherSkillRepository teacherSkillRepository,
                        IUserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.teacherSkillRepository = teacherSkillRepository;
        this.userRepository = userRepository;
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public List<SkillResponse> getAllSkillResponses() {
        return skillRepository.findAllSkillResponses();
    }

    public void addSkillToTeacher(Long teacherId, Long skillId) {

        if (teacherId == null || skillId == null) {
            throw new IllegalArgumentException("TeacherId and SkillId must be provided");
        }

        User user = userRepository.findById(teacherId);

        if (user == null) {
            throw new IllegalArgumentException("User does not exist");
        }

        if (user.getRole() != Role.TEACHER) {
            throw new IllegalArgumentException("Only teachers can have skills");
        }

        Skill skill = skillRepository.findById(skillId);

        if (skill == null) {
            throw new IllegalArgumentException("Skill does not exist");
        }

        teacherSkillRepository.saveByIds(teacherId, skillId);
    }

    public List<Skill> getSkillsByTeacher(Long teacherId) {

        if (teacherId == null) {
            throw new IllegalArgumentException("TeacherId cannot be null");
        }

        return teacherSkillRepository.findSkillsByTeacherId(teacherId);
    }
}