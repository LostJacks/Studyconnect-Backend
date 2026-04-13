package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.AvailabilityService;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.presentation.dto.CreateAvailabilityRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping("/availability")
    public List<AvailabilitySlot> createAvailability(
            @RequestBody CreateAvailabilityRequest request) {

        return availabilityService.createAvailability(request);
    }

    @GetMapping("/teachers/{teacherId}/availability")
    public List<AvailabilitySlot> getTeacherAvailability(
            @PathVariable Long teacherId) {

        return availabilityService.getTeacherAvailability(teacherId);
    }
}