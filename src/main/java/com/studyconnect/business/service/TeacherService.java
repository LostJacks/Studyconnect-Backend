package com.studyconnect.business.service;

import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.ISkillRepository;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.presentation.dto.CompleteTeacherProfileRequest;
import com.studyconnect.presentation.dto.SkillResponse;
import com.studyconnect.presentation.dto.TeacherResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherService {

    private final IUserRepository userRepository;
    private final ISkillRepository skillRepository;

    public TeacherService(IUserRepository userRepository, ISkillRepository skillRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    public List<TeacherResponse> getAllTeachers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> user.getRole().equals("TEACHER"))
                .map(this::mapToTeacherResponse)
                .collect(Collectors.toList());
    }

    public void completeTeacherProfile(Long teacherId, CompleteTeacherProfileRequest request) {
        validateTeacherProfile(teacherId, request);

        userRepository.updateBio(teacherId, request.getBio());

        skillRepository.deleteTeacherSkillsByTeacherId(teacherId);

        for (Long skillId : request.getSkillIds()) {
            skillRepository.addTeacherSkill(teacherId, skillId);
        }
    }

    private void validateTeacherProfile(Long teacherId, CompleteTeacherProfileRequest request) {
        if (teacherId == null) {
            throw new IllegalArgumentException("Teacher id is required");
        }

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getBio() == null || request.getBio().isBlank()) {
            throw new IllegalArgumentException("Bio is required");
        }

        if (request.getSkillIds() == null || request.getSkillIds().isEmpty()) {
            throw new IllegalArgumentException("At least one skill is required");
        }
    }

    private TeacherResponse mapToTeacherResponse(UserEntity user) {
        List<SkillResponse> skills = skillRepository.findSkillsByTeacherId(user.getId());

        return new TeacherResponse(
                user.getId(),
                user.getName(),
                user.getBio(),
                skills
        );
    }
}