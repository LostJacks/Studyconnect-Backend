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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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
        UserEntity teacher = new UserEntity(1L, "Teacher User", "teacher@test.com", "password", "TEACHER");
        UserEntity student = new UserEntity(2L, "Student User", "student@test.com", "password", "STUDENT");

        List<SkillResponse> skills = List.of(new SkillResponse(1L, "Java"));

        when(userRepository.findAll()).thenReturn(List.of(teacher, student));
        when(skillRepository.findSkillsByTeacherId(1L)).thenReturn(skills);

        List<TeacherResponse> result = teacherService.getAllTeachers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Teacher User", result.get(0).getName());
        assertEquals(skills, result.get(0).getSkills());

        verify(userRepository).findAll();
        verify(skillRepository).findSkillsByTeacherId(1L);
        verify(skillRepository, never()).findSkillsByTeacherId(2L);
    }

    @Test
    void getAllTeachers_noTeachers_returnsEmptyList() {
        UserEntity student = new UserEntity(2L, "Student User", "student@test.com", "password", "STUDENT");

        when(userRepository.findAll()).thenReturn(List.of(student));

        List<TeacherResponse> result = teacherService.getAllTeachers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
        verifyNoInteractions(skillRepository);
    }

    @Test
    void getAvailableTeachers_nullDateAndMatchingTime_returnsTeacher() {
        UserEntity teacher = new UserEntity(1L, "Teacher User", "teacher@test.com", "password", "TEACHER");

        AvailabilitySlot slot = new AvailabilitySlot(
                LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                false
        );

        when(userRepository.findAll()).thenReturn(List.of(teacher));
        when(availabilityRepository.findByTeacherId(1L)).thenReturn(List.of(slot));
        when(skillRepository.findSkillsByTeacherId(1L)).thenReturn(List.of(new SkillResponse(1L, "Java")));

        List<TeacherResponse> result = teacherService.getAvailableTeachers(
                null,
                LocalTime.of(10, 0)
        );

        assertEquals(1, result.size());
    }

    @Test
    void getAvailableTeachers_matchingDateAndNullStartTime_returnsTeacher() {
        UserEntity teacher = new UserEntity(1L, "Teacher User", "teacher@test.com", "password", "TEACHER");

        AvailabilitySlot slot = new AvailabilitySlot(
                LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                false
        );

        when(userRepository.findAll()).thenReturn(List.of(teacher));
        when(availabilityRepository.findByTeacherId(1L)).thenReturn(List.of(slot));
        when(skillRepository.findSkillsByTeacherId(1L)).thenReturn(List.of(new SkillResponse(1L, "Java")));

        List<TeacherResponse> result = teacherService.getAvailableTeachers(
                LocalDate.of(2026, 3, 20),
                null
        );

        assertEquals(1, result.size());
    }

    @Test
    void getAvailableTeachers_dateAndStartTimeMatch_returnsTeacher() {
        UserEntity teacher = new UserEntity(1L, "Teacher User", "teacher@test.com", "password", "TEACHER");

        AvailabilitySlot slot = new AvailabilitySlot(
                LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                false
        );

        List<SkillResponse> skills = List.of(new SkillResponse(1L, "Java"));

        when(userRepository.findAll()).thenReturn(List.of(teacher));
        when(availabilityRepository.findByTeacherId(1L)).thenReturn(List.of(slot));
        when(skillRepository.findSkillsByTeacherId(1L)).thenReturn(skills);

        List<TeacherResponse> result = teacherService.getAvailableTeachers(
                LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0)
        );

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Teacher User", result.get(0).getName());

        verify(userRepository).findAll();
        verify(availabilityRepository).findByTeacherId(1L);
        verify(skillRepository).findSkillsByTeacherId(1L);
    }

    @ParameterizedTest
    @CsvSource({
            "2026-03-21,10:00,false",
            "2026-03-20,11:00,false",
            "2026-03-20,10:00,true"
    })
    void getAvailableTeachers_noMatchingAvailableSlot_returnsEmptyList(
            LocalDate searchDate,
            LocalTime searchTime,
            boolean booked
    ) {
        UserEntity teacher = new UserEntity(1L, "Teacher User", "teacher@test.com", "password", "TEACHER");

        AvailabilitySlot slot = new AvailabilitySlot(
                LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                booked
        );

        when(userRepository.findAll()).thenReturn(List.of(teacher));
        when(availabilityRepository.findByTeacherId(1L)).thenReturn(List.of(slot));

        List<TeacherResponse> result =
                teacherService.getAvailableTeachers(searchDate, searchTime);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
        verify(availabilityRepository).findByTeacherId(1L);
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