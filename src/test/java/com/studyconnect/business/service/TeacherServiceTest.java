package com.studyconnect.business.service;

import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.data.irepository.ISkillRepository;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.presentation.dto.CompleteTeacherProfileRequest;
import com.studyconnect.presentation.dto.PagedResponse;
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

        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
        verifyNoInteractions(skillRepository);
    }

    @Test
    void getTeachers_returnsPagedTeachers() {
        UserEntity teacher = new UserEntity(1L, "Teacher User", "teacher@test.com", "password", "TEACHER");

        when(userRepository.findTeachersPaged(5, 0)).thenReturn(List.of(teacher));
        when(userRepository.countTeachers()).thenReturn(1L);
        when(skillRepository.findSkillsByTeacherId(1L)).thenReturn(List.of(new SkillResponse(1L, "Java")));

        PagedResponse<TeacherResponse> result = teacherService.getTeachers(0, 5);

        assertEquals(1, result.getContent().size());
        assertEquals(0, result.getCurrentPage());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getTotalItems());
        assertEquals(5, result.getPageSize());
    }

    @Test
    void getTeachers_negativePage_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> teacherService.getTeachers(-1, 5));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(skillRepository);
    }

    @Test
    void getTeachers_zeroSize_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> teacherService.getTeachers(0, 0));

        verifyNoInteractions(userRepository);
        verifyNoInteractions(skillRepository);
    }

    @Test
    void getTeachers_multiplePages_calculatesTotalPagesCorrectly() {
        when(userRepository.findTeachersPaged(5, 5)).thenReturn(List.of());
        when(userRepository.countTeachers()).thenReturn(11L);

        PagedResponse<TeacherResponse> result = teacherService.getTeachers(1, 5);

        assertEquals(1, result.getCurrentPage());
        assertEquals(3, result.getTotalPages());
        assertEquals(11, result.getTotalItems());
        assertEquals(5, result.getPageSize());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void getAvailableTeachers_nullDateAndMatchingStartTime_returnsTeacher() {
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

        List<TeacherResponse> result = teacherService.getAvailableTeachers(null, LocalTime.of(10, 0), null);

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

        List<TeacherResponse> result = teacherService.getAvailableTeachers(LocalDate.of(2026, 3, 20), null, null);

        assertEquals(1, result.size());
    }

    @Test
    void getAvailableTeachers_dateStartAndEndTimeMatch_returnsTeacher() {
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
                LocalTime.of(10, 0),
                LocalTime.of(10, 30)
        );

        assertEquals(1, result.size());
        assertEquals("Teacher User", result.get(0).getName());
    }

    @Test
    void getAvailableTeachers_dateAndStartTimeMatchButEndTimeDifferent_returnsEmptyList() {
        UserEntity teacher = new UserEntity(1L, "Teacher User", "teacher@test.com", "password", "TEACHER");

        AvailabilitySlot slot = new AvailabilitySlot(
                LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                false
        );

        when(userRepository.findAll()).thenReturn(List.of(teacher));
        when(availabilityRepository.findByTeacherId(1L)).thenReturn(List.of(slot));

        List<TeacherResponse> result = teacherService.getAvailableTeachers(
                LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0),
                LocalTime.of(11, 0)
        );

        assertTrue(result.isEmpty());

        verifyNoInteractions(skillRepository);
    }

    @ParameterizedTest
    @CsvSource({
            "2026-03-21,10:00,10:30,false",
            "2026-03-20,11:00,10:30,false",
            "2026-03-20,10:00,10:30,true"
    })
    void getAvailableTeachers_noMatchingAvailableSlot_returnsEmptyList(
            LocalDate searchDate,
            LocalTime searchStartTime,
            LocalTime searchEndTime,
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

        List<TeacherResponse> result = teacherService.getAvailableTeachers(
                searchDate,
                searchStartTime,
                searchEndTime
        );

        assertTrue(result.isEmpty());

        verifyNoInteractions(skillRepository);
    }

    @Test
    void getAvailableTeachers_nonTeacherUser_isIgnored() {
        UserEntity student = new UserEntity(2L, "Student User", "student@test.com", "password", "STUDENT");

        when(userRepository.findAll()).thenReturn(List.of(student));

        List<TeacherResponse> result = teacherService.getAvailableTeachers(
                LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30)
        );

        assertTrue(result.isEmpty());

        verifyNoInteractions(availabilityRepository);
        verifyNoInteractions(skillRepository);
    }

    @Test
    void getTeacherById_existingTeacher_returnsTeacher() {
        UserEntity teacher = new UserEntity(1L, "Teacher User", "teacher@test.com", "password", "TEACHER");

        when(userRepository.findEntityById(1L)).thenReturn(teacher);
        when(skillRepository.findSkillsByTeacherId(1L)).thenReturn(List.of(new SkillResponse(1L, "Java")));

        TeacherResponse result = teacherService.getTeacherById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Teacher User", result.getName());
        assertEquals(1, result.getSkills().size());
    }

    @Test
    void getTeacherById_teacherDoesNotExist_throwsException() {
        when(userRepository.findEntityById(1L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> teacherService.getTeacherById(1L));

        verifyNoInteractions(skillRepository);
    }

    @Test
    void getTeacherById_userIsNotTeacher_throwsException() {
        UserEntity student = new UserEntity(1L, "Student User", "student@test.com", "password", "STUDENT");

        when(userRepository.findEntityById(1L)).thenReturn(student);

        assertThrows(IllegalArgumentException.class, () -> teacherService.getTeacherById(1L));

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