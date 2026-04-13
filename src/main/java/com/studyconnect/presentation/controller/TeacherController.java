package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.TeacherService;
import com.studyconnect.presentation.dto.CompleteTeacherProfileRequest;
import com.studyconnect.presentation.dto.TeacherResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping
    public List<TeacherResponse> getTeachers() {
        return teacherService.getAllTeachers();
    }

    @PutMapping("/{teacherId}/profile")
    public ResponseEntity<Void> completeProfile(
            @PathVariable Long teacherId,
            @RequestBody CompleteTeacherProfileRequest request) {
        teacherService.completeTeacherProfile(teacherId, request);
        return ResponseEntity.ok().build();
    }
}