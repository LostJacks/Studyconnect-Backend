package com.studyconnect.business.service;

import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.data.irepository.ISkillRepository;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.presentation.dto.CompleteTeacherProfileRequest;
import com.studyconnect.presentation.dto.SkillResponse;
import com.studyconnect.presentation.dto.TeacherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherServiceTest {

    private IUserRepository userRepository;
    private ISkillRepository skillRepository;
    private IAvailabilityRepository availabilityRepository;
    private TeacherService teacherService;

    @BeforeEach
    void setUp() {
        userRepository = mock(IUserRepository.class);
        skillRepository = mock(ISkillRepository.class);
        availabilityRepository = mock(IAvailabilityRepository.class);

        teacherService = new TeacherService(
                userRepository,
                skillRepository,
                availabilityRepository
        );
    }

    @Test
    void getAllTeachers_returnsOnlyTeachers() {
        UserEntity teacher = new UserEntity(
                1L,
                "Teacher User",
                "teacher@test.com",
                "password",
                "TEACHER"
        );

        UserEntity student = new UserEntity(
                2L,
                "Student User",
                "student@test.com",
                "password",
                "STUDENT"
        );

        List<SkillResponse> skills = List.of(
                new SkillResponse(1L, "Java")
        );

        when(userRepository.findAll()).thenReturn(List.of(teacher, student));
        when(skillRepository.findSkillsByTeacherId(1L)).thenReturn(skills);

        List<TeacherResponse> result = teacherService.getAllTeachers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Teacher User", result.get(0).getName());
        assertEquals(skills, result.get(0).getSkills());

        verify(userRepository).findAll();
        verify(skillRepository).findSkillsByTeacherId(1L);
        verify(skillRepository, never()).findSkillsByTeacherId(2L);
    }

    @Test
    void getAllTeachers_noTeachers_returnsEmptyList() {
        UserEntity student = new UserEntity(
                2L,
                "Student User",
                "student@test.com",
                "password",
                "STUDENT"
        );

        when(userRepository.findAll()).thenReturn(List.of(student));

        List<TeacherResponse> result = teacherService.getAllTeachers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
        verifyNoInteractions(skillRepository);
    }

    @Test
    void completeTeacherProfile_validRequest_updatesBioImageAndSkills() {
        CompleteTeacherProfileRequest request = new CompleteTeacherProfileRequest();
        request.setBio("I teach software engineering.");
        request.setProfileImageUrl("teacher-avatar.png");
        request.setSkillIds(List.of(1L, 2L));

        teacherService.completeTeacherProfile(10L, request);

        verify(userRepository).updateBio(10L, "I teach software engineering.");
        verify(userRepository).updateProfileImage(10L, "teacher-avatar.png");
        verify(skillRepository).deleteTeacherSkillsByTeacherId(10L);
        verify(skillRepository).addTeacherSkill(10L, 1L);
        verify(skillRepository).addTeacherSkill(10L, 2L);
    }
}