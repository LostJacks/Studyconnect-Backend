package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.AvailabilityService;
import com.studyconnect.domain.entity.AvailabilitySlot;
import com.studyconnect.presentation.dto.CreateAvailabilityRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/availability")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping
    public AvailabilitySlot createAvailability(
            @RequestBody CreateAvailabilityRequest request) {

        return availabilityService.createAvailability(request);
    }
}