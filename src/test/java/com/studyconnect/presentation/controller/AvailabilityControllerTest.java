package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.IAvailabilityService;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.presentation.dto.CreateAvailabilityRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class AvailabilityControllerTest {

    private final IAvailabilityService availabilityService =
            mock(IAvailabilityService.class);

    private final AvailabilityController availabilityController =
            new AvailabilityController(availabilityService);

    @Test
    void createAvailability_returnsCreatedSlots() {
        CreateAvailabilityRequest request =
                mock(CreateAvailabilityRequest.class);

        List<AvailabilitySlot> slots = List.of(
                mock(AvailabilitySlot.class)
        );

        when(availabilityService.createAvailability(request))
                .thenReturn(slots);

        List<AvailabilitySlot> result =
                availabilityController.createAvailability(request);

        assertSame(slots, result);

        verify(availabilityService).createAvailability(request);
    }
}