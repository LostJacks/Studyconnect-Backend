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

    public void completeTeacherProfile(CompleteTeacherProfileRequest request) {
        validateTeacherProfile(request);

        userRepository.updateBio(request.getTeacherId(), request.getBio());

        skillRepository.deleteTeacherSkillsByTeacherId(request.getTeacherId());

        for (Long skillId : request.getSkillIds()) {
            skillRepository.addTeacherSkill(request.getTeacherId(), skillId);
        }
    }

    private void validateTeacherProfile(CompleteTeacherProfileRequest request) {
        if (request.getTeacherId() == null) {
            throw new IllegalArgumentException("Teacher id is required");
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