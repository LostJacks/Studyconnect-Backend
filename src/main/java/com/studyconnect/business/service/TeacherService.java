package com.studyconnect.business.service;

import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.data.irepository.ISkillRepository;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.presentation.dto.CompleteTeacherProfileRequest;
import com.studyconnect.presentation.dto.PagedResponse;
import com.studyconnect.presentation.dto.SkillResponse;
import com.studyconnect.presentation.dto.TeacherResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class TeacherService implements ITeacherService {

    private static final String ROLE_TEACHER = "TEACHER";

    private final IUserRepository userRepository;
    private final ISkillRepository skillRepository;
    private final IAvailabilityRepository availabilityRepository;

    public TeacherService(
            IUserRepository userRepository,
            ISkillRepository skillRepository,
            IAvailabilityRepository availabilityRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
        this.availabilityRepository = availabilityRepository;
    }

    @Override
    public List<TeacherResponse> getAllTeachers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> ROLE_TEACHER.equals(user.getRole()))
                .map(this::mapToTeacherResponse)
                .toList();
    }

    @Override
    public PagedResponse<TeacherResponse> getTeachers(int page, int size) {
        validatePagination(page, size);

        int offset = page * size;

        List<TeacherResponse> teachers = userRepository.findTeachersPaged(size, offset)
                .stream()
                .map(this::mapToTeacherResponse)
                .toList();

        long totalItems = userRepository.countTeachers();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        return new PagedResponse<>(
                teachers,
                page,
                totalPages,
                totalItems,
                size
        );
    }

    @Override
    public List<TeacherResponse> getAvailableTeachers(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime
    ) {

        return userRepository.findAll()
                .stream()
                .filter(user -> ROLE_TEACHER.equals(user.getRole()))
                .filter(user -> availabilityRepository
                        .findByTeacherId(user.getId())
                        .stream()
                        .anyMatch(slot -> {

                            boolean matchesDate =
                                    date == null ||
                                            slot.getDate().equals(date);

                            boolean matchesStart =
                                    startTime == null ||
                                            slot.getStartTime().equals(startTime);

                            boolean matchesEnd =
                                    endTime == null ||
                                            slot.getEndTime().equals(endTime);

                            boolean isAvailable =
                                    !slot.isBooked();

                            return matchesDate
                                    && matchesStart
                                    && matchesEnd
                                    && isAvailable;
                        })
                )
                .map(this::mapToTeacherResponse)
                .toList();
    }

    @Override
    public TeacherResponse getTeacherById(Long teacherId) {
        UserEntity teacher = userRepository.findEntityById(teacherId);

        if (teacher == null || !ROLE_TEACHER.equals(teacher.getRole())) {
            throw new IllegalArgumentException("Teacher not found");
        }

        return mapToTeacherResponse(teacher);
    }

    @Override
    public void completeTeacherProfile(Long teacherId, CompleteTeacherProfileRequest request) {
        userRepository.updateBio(teacherId, request.getBio());
        userRepository.updateProfileImage(teacherId, request.getProfileImageUrl());

        skillRepository.deleteTeacherSkillsByTeacherId(teacherId);

        for (Long skillId : request.getSkillIds()) {
            skillRepository.addTeacherSkill(teacherId, skillId);
        }
    }

    private void validatePagination(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number cannot be negative");
        }

        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero");
        }
    }

    private TeacherResponse mapToTeacherResponse(UserEntity user) {
        List<SkillResponse> skills = skillRepository.findSkillsByTeacherId(user.getId());

        return new TeacherResponse(
                user.getId(),
                user.getName(),
                user.getBio(),
                user.getProfileImageUrl(),
                skills
        );
    }
}