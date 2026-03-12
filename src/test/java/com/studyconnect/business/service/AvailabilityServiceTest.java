package com.studyconnect.business.service;

import com.studyconnect.domain.entity.AvailabilitySlot;
import com.studyconnect.presentation.dto.CreateAvailabilityRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class AvailabilityServiceTest {

    private final AvailabilityService availabilityService = new AvailabilityService();

    @Test
    void createAvailability_validRequest_returnsAvailabilitySlot() {

        CreateAvailabilityRequest request = new CreateAvailabilityRequest();
        request.setHelperId(1L);
        request.setDate(LocalDate.of(2026,3,12));
        request.setStartTime(LocalTime.of(10,0));
        request.setEndTime(LocalTime.of(12,0));

        AvailabilitySlot result = availabilityService.createAvailability(request);

        assertNotNull(result);
        assertEquals(1L, result.getHelperId());
    }

    @Test
    void createAvailability_startAfterEnd_throwsException() {

        CreateAvailabilityRequest request = new CreateAvailabilityRequest();
        request.setHelperId(1L);
        request.setDate(LocalDate.of(2026,3,12));
        request.setStartTime(LocalTime.of(12,0));
        request.setEndTime(LocalTime.of(10,0));

        assertThrows(IllegalArgumentException.class, () -> {
            availabilityService.createAvailability(request);
        });
    }
}