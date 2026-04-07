package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.SkillService;
import com.studyconnect.domain.model.Skill;
import com.studyconnect.presentation.dto.SkillResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @GetMapping
    public List<SkillResponse> getAllSkills() {
        return skillService.getAllSkillResponses();
    }

    @PostMapping("/teacher/{teacherId}/{skillId}")
    public void addSkillToTeacher(
            @PathVariable Long teacherId,
            @PathVariable Long skillId) {
        skillService.addSkillToTeacher(teacherId, skillId);
    }

    @GetMapping("/teacher/{teacherId}")
    public List<Skill> getSkillsByTeacher(@PathVariable Long teacherId) {
        return skillService.getSkillsByTeacher(teacherId);
    }
}