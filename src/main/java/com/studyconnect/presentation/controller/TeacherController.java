package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.IAvailabilityService;
import com.studyconnect.business.service.ITeacherService;
import com.studyconnect.presentation.dto.AvailabilitySlotResponse;
import com.studyconnect.presentation.dto.CompleteTeacherProfileRequest;
import com.studyconnect.presentation.dto.PagedResponse;
import com.studyconnect.presentation.dto.TeacherResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/teachers")
public class TeacherController {

    private final ITeacherService teacherService;
    private final IAvailabilityService availabilityService;

    public TeacherController(
            ITeacherService teacherService,
            IAvailabilityService availabilityService) {
        this.teacherService = teacherService;
        this.availabilityService = availabilityService;
    }

    @GetMapping
    public PagedResponse<TeacherResponse> getTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        return teacherService.getTeachers(page, size);
    }

    @GetMapping("/available")
    public List<TeacherResponse> getAvailableTeachers(
            @RequestParam(required = false) LocalDate date,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalTime endTime) {

        return teacherService.getAvailableTeachers(date, startTime, endTime);
    }

    @GetMapping("/{teacherId}/availability")
    public List<AvailabilitySlotResponse> getTeacherAvailability(
            @PathVariable Long teacherId) {

        return availabilityService.getTeacherAvailability(teacherId);
    }

    @PutMapping("/{teacherId}/profile")
    public ResponseEntity<Void> completeProfile(
            @PathVariable Long teacherId,
            @Valid @RequestBody CompleteTeacherProfileRequest request) {

        teacherService.completeTeacherProfile(teacherId, request);
        return ResponseEntity.ok().build();
    }
}