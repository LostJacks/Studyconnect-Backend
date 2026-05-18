package com.studyconnect.business.service;

import com.studyconnect.data.irepository.ISkillRepository;
import com.studyconnect.data.irepository.ITeacherSkillRepository;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.domain.model.Role;
import com.studyconnect.domain.model.Skill;
import com.studyconnect.domain.model.User;
import com.studyconnect.presentation.dto.SkillResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SkillServiceTest {

    private ISkillRepository skillRepository;
    private ITeacherSkillRepository teacherSkillRepository;
    private IUserRepository userRepository;
    private SkillService skillService;

    @BeforeEach
    void setUp() {
        skillRepository = mock(ISkillRepository.class);
        teacherSkillRepository = mock(ITeacherSkillRepository.class);
        userRepository = mock(IUserRepository.class);

        skillService = new SkillService(
                skillRepository,
                teacherSkillRepository,
                userRepository
        );
    }

    @Test
    void getAllSkills_returnsSkills() {
        List<Skill> skills = List.of(
                new Skill("Java"),
                new Skill("React")
        );

        when(skillRepository.findAll()).thenReturn(skills);

        List<Skill> result = skillService.getAllSkills();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getName());
        assertEquals("React", result.get(1).getName());

        verify(skillRepository).findAll();
    }

    @Test
    void getAllSkillResponses_returnsSkillResponses() {
        List<SkillResponse> responses = List.of(
                new SkillResponse(1L, "Java"),
                new SkillResponse(2L, "React")
        );

        when(skillRepository.findAllSkillResponses()).thenReturn(responses);

        List<SkillResponse> result = skillService.getAllSkillResponses();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Java", result.get(0).getName());
        assertEquals(2L, result.get(1).getId());
        assertEquals("React", result.get(1).getName());

        verify(skillRepository).findAllSkillResponses();
    }

    @Test
    void addSkillToTeacher_validTeacherAndSkill_savesSkill() {
        User teacher = new User(
                "Teacher User",
                "teacher@test.com",
                "encodedPassword",
                Role.TEACHER
        );

        Skill skill = new Skill("Java");

        when(userRepository.findById(10L)).thenReturn(teacher);
        when(skillRepository.findById(1L)).thenReturn(skill);

        skillService.addSkillToTeacher(10L, 1L);

        verify(userRepository).findById(10L);
        verify(skillRepository).findById(1L);
        verify(teacherSkillRepository).saveByIds(10L, 1L);
    }

    @Test
    void addSkillToTeacher_userDoesNotExist_throwsException() {
        when(userRepository.findById(10L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> skillService.addSkillToTeacher(10L, 1L)
        );

        assertEquals("User does not exist", exception.getMessage());

        verify(userRepository).findById(10L);
        verifyNoInteractions(skillRepository);
        verifyNoInteractions(teacherSkillRepository);
    }

    @Test
    void addSkillToTeacher_userIsStudent_throwsException() {
        User student = new User(
                "Student User",
                "student@test.com",
                "encodedPassword",
                Role.STUDENT
        );

        when(userRepository.findById(10L)).thenReturn(student);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> skillService.addSkillToTeacher(10L, 1L)
        );

        assertEquals("Only teachers can have skills", exception.getMessage());

        verify(userRepository).findById(10L);
        verifyNoInteractions(skillRepository);
        verifyNoInteractions(teacherSkillRepository);
    }

    @Test
    void addSkillToTeacher_skillDoesNotExist_throwsException() {
        User teacher = new User(
                "Teacher User",
                "teacher@test.com",
                "encodedPassword",
                Role.TEACHER
        );

        when(userRepository.findById(10L)).thenReturn(teacher);
        when(skillRepository.findById(1L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> skillService.addSkillToTeacher(10L, 1L)
        );

        assertEquals("Skill does not exist", exception.getMessage());

        verify(userRepository).findById(10L);
        verify(skillRepository).findById(1L);
        verifyNoInteractions(teacherSkillRepository);
    }

    @Test
    void getSkillsByTeacher_returnsSkills() {
        List<Skill> skills = List.of(
                new Skill("Java"),
                new Skill("React")
        );

        when(teacherSkillRepository.findSkillsByTeacherId(10L)).thenReturn(skills);

        List<Skill> result = skillService.getSkillsByTeacher(10L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Java", result.get(0).getName());
        assertEquals("React", result.get(1).getName());

        verify(teacherSkillRepository).findSkillsByTeacherId(10L);
    }
}