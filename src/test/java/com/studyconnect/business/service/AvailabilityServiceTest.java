package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.presentation.dto.CreateAvailabilityRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class AvailabilityServiceTest {

    private IAvailabilityRepository availabilityRepository;
    private AvailabilityService availabilityService;

    @BeforeEach
    void setUp() {
        availabilityRepository = mock(IAvailabilityRepository.class);
        availabilityService = new AvailabilityService(availabilityRepository);
    }

    @Test
    void createAvailability_validRequest_returnsAvailabilitySlot() {

        CreateAvailabilityRequest request = new CreateAvailabilityRequest();
        request.setTeacherId(1L);
        request.setDate(LocalDate.of(2026, 3, 12));
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(12, 0));

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
        assertEquals(1L, result.getTeacherId());
        verify(availabilityRepository, times(1)).save(any());
    }

    @Test
    void createAvailability_startAfterEnd_throwsException() {

        CreateAvailabilityRequest request = new CreateAvailabilityRequest();
        request.setTeacherId(1L);
        request.setDate(LocalDate.of(2026, 3, 12));
        request.setStartTime(LocalTime.of(12, 0));
        request.setEndTime(LocalTime.of(10, 0));

        assertThrows(IllegalArgumentException.class, () ->
                availabilityService.createAvailability(request)
        );

        verify(availabilityRepository, never()).save(any());
    }

    @Test
    void createAvailability_nullRequest_throwsException() {

        assertThrows(IllegalArgumentException.class, () ->
                availabilityService.createAvailability(null)
        );

        verify(availabilityRepository, never()).save(any());
    }

    @Test
    void createAvailability_missingField_throwsException() {

        CreateAvailabilityRequest request = new CreateAvailabilityRequest();
        request.setTeacherId(1L);
        request.setDate(null);
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(12, 0));

        assertThrows(IllegalArgumentException.class, () ->
                availabilityService.createAvailability(request)
        );

        verify(availabilityRepository, never()).save(any());
    }

    @Test
    void getTeacherAvailability_valid_returnsList() {

        when(availabilityRepository.findByTeacherId(1L)).thenReturn(List.of());

        var result = availabilityService.getTeacherAvailability(1L);

        assertNotNull(result);
        verify(availabilityRepository, times(1)).findByTeacherId(1L);
    }

    @Test
    void getTeacherAvailability_null_throwsException() {

        assertThrows(IllegalArgumentException.class, () ->
                availabilityService.getTeacherAvailability(null)
        );

        verify(availabilityRepository, never()).findByTeacherId(any());
    }
}