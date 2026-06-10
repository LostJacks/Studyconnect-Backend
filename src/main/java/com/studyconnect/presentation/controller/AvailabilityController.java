package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.IAvailabilityService;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.presentation.dto.CreateAvailabilityRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AvailabilityController {

    private final IAvailabilityService availabilityService;

    public AvailabilityController(IAvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping("/availability")
    public List<AvailabilitySlot> createAvailability(
            @Valid @RequestBody CreateAvailabilityRequest request) {

        return availabilityService.createAvailability(request);
    }

    @PatchMapping("/availability/{slotId}/status")
    public ResponseEntity<Void> updateAvailabilityStatus(
            @PathVariable Long slotId,
            @RequestParam boolean available
    ) {
        availabilityService.updateAvailabilityStatus(slotId, available);

        return ResponseEntity.ok().build();
    }
}