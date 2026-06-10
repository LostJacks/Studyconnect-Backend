package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.ISkillService;
import com.studyconnect.domain.model.Skill;
import com.studyconnect.presentation.dto.SkillResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class SkillControllerTest {

    private final ISkillService skillService = mock(ISkillService.class);

    private final SkillController skillController =
            new SkillController(skillService);

    @Test
    void getAllSkills_returnsSkills() {
        List<SkillResponse> skills =
                List.of(new SkillResponse(1L, "Java"));

        when(skillService.getAllSkillResponses())
                .thenReturn(skills);

        List<SkillResponse> result =
                skillController.getAllSkills();

        assertSame(skills, result);

        verify(skillService).getAllSkillResponses();
    }

    @Test
    void addSkillToTeacher_callsService() {
        skillController.addSkillToTeacher(1L, 2L);

        verify(skillService).addSkillToTeacher(1L, 2L);
    }

    @Test
    void getSkillsByTeacher_returnsSkills() {
        List<Skill> skills =
                List.of(new Skill("Java"));

        when(skillService.getSkillsByTeacher(1L))
                .thenReturn(skills);

        List<Skill> result =
                skillController.getSkillsByTeacher(1L);

        assertSame(skills, result);

        verify(skillService).getSkillsByTeacher(1L);
    }
}