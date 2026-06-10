package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.ISkillService;
import com.studyconnect.domain.model.Skill;
import com.studyconnect.presentation.dto.SkillResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SkillController {

    private final ISkillService skillService;

    public SkillController(ISkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping("/skills")
    public List<SkillResponse> getAllSkills() {
        return skillService.getAllSkillResponses();
    }

    @PostMapping("/teachers/{teacherId}/skills/{skillId}")
    public void addSkillToTeacher(
            @PathVariable Long teacherId,
            @PathVariable Long skillId) {
        skillService.addSkillToTeacher(teacherId, skillId);
    }

    @GetMapping("/teachers/{teacherId}/skills")
    public List<Skill> getSkillsByTeacher(@PathVariable Long teacherId) {
        return skillService.getSkillsByTeacher(teacherId);
    }
}