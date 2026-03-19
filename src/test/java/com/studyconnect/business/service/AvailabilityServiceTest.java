package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.presentation.dto.CreateAvailabilityRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AvailabilityServiceTest {

    private IAvailabilityRepository availabilityRepository;
    private AvailabilityService availabilityService;

    @BeforeEach
    void setUp() {
        availabilityRepository = Mockito.mock(IAvailabilityRepository.class);
        availabilityService = new AvailabilityService(availabilityRepository);
    }

    @Test
    void createAvailability_validRequest_returnsAvailabilitySlot() {

        CreateAvailabilityRequest request = new CreateAvailabilityRequest();
        request.setHelperId(1L);
        request.setDate(LocalDate.of(2026,3,12));
        request.setStartTime(LocalTime.of(10,0));
        request.setEndTime(LocalTime.of(12,0));

        AvailabilitySlot savedSlot = new AvailabilitySlot(
                1L,
                1L,
                request.getDate(),
                request.getStartTime(),
                request.getEndTime(),
                false
        );

        when(availabilityRepository.save(any())).thenReturn(savedSlot);

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