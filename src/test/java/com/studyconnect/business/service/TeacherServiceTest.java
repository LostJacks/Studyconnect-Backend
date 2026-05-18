package com.studyconnect.business.service;

import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.data.irepository.ISkillRepository;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.presentation.dto.CompleteTeacherProfileRequest;
import com.studyconnect.presentation.dto.SkillResponse;
import com.studyconnect.presentation.dto.TeacherResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
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
    void getAvailableTeachers_teacherHasMatchingAvailableSlot_returnsTeacher() {
        LocalDate date = LocalDate.of(2026, 3, 20);
        LocalTime startTime = LocalTime.of(10, 0);

        UserEntity teacher = new UserEntity(
                1L,
                "Teacher User",
                "teacher@test.com",
                "password",
                "TEACHER"
        );

        AvailabilitySlot slot = new AvailabilitySlot(
                date,
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                false
        );

        List<SkillResponse> skills = List.of(
                new SkillResponse(1L, "Java")
        );

        when(userRepository.findAll()).thenReturn(List.of(teacher));
        when(availabilityRepository.findByTeacherAndDate(1L, date))
                .thenReturn(List.of(slot));
        when(skillRepository.findSkillsByTeacherId(1L)).thenReturn(skills);

        List<TeacherResponse> result =
                teacherService.getAvailableTeachers(date, startTime);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Teacher User", result.get(0).getName());
        assertEquals(skills, result.get(0).getSkills());

        verify(userRepository).findAll();
        verify(availabilityRepository).findByTeacherAndDate(1L, date);
        verify(skillRepository).findSkillsByTeacherId(1L);
    }

    @Test
    void getAvailableTeachers_slotIsBooked_returnsEmptyList() {
        LocalDate date = LocalDate.of(2026, 3, 20);
        LocalTime startTime = LocalTime.of(10, 0);

        UserEntity teacher = new UserEntity(
                1L,
                "Teacher User",
                "teacher@test.com",
                "password",
                "TEACHER"
        );

        AvailabilitySlot bookedSlot = new AvailabilitySlot(
                date,
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                true
        );

        when(userRepository.findAll()).thenReturn(List.of(teacher));
        when(availabilityRepository.findByTeacherAndDate(1L, date))
                .thenReturn(List.of(bookedSlot));

        List<TeacherResponse> result =
                teacherService.getAvailableTeachers(date, startTime);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
        verify(availabilityRepository).findByTeacherAndDate(1L, date);
        verify(skillRepository, never()).findSkillsByTeacherId(anyLong());
    }

    @Test
    void getAvailableTeachers_slotTimeDoesNotMatch_returnsEmptyList() {
        LocalDate date = LocalDate.of(2026, 3, 20);
        LocalTime startTime = LocalTime.of(11, 0);

        UserEntity teacher = new UserEntity(
                1L,
                "Teacher User",
                "teacher@test.com",
                "password",
                "TEACHER"
        );

        AvailabilitySlot slot = new AvailabilitySlot(
                date,
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                false
        );

        when(userRepository.findAll()).thenReturn(List.of(teacher));
        when(availabilityRepository.findByTeacherAndDate(1L, date))
                .thenReturn(List.of(slot));

        List<TeacherResponse> result =
                teacherService.getAvailableTeachers(date, startTime);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
        verify(availabilityRepository).findByTeacherAndDate(1L, date);
        verify(skillRepository, never()).findSkillsByTeacherId(anyLong());
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