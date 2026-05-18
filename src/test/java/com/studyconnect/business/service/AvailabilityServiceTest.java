package com.studyconnect.business.service;

import com.studyconnect.data.entity.AvailabilitySlotEntity;
import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.presentation.dto.AvailabilitySlotResponse;
import com.studyconnect.presentation.dto.CreateAvailabilityRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AvailabilityServiceTest {

    private IAvailabilityRepository availabilityRepository;
    private AvailabilityService availabilityService;

    @BeforeEach
    void setUp() {
        availabilityRepository = mock(IAvailabilityRepository.class);
        availabilityService = new AvailabilityService(availabilityRepository);
    }

    @Test
    void createAvailability_validRequest_returnsAvailabilitySlots() {
        CreateAvailabilityRequest request = new CreateAvailabilityRequest();
        request.setTeacherId(1L);
        request.setDate(LocalDate.of(2026, 3, 12));
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(12, 0));

        when(availabilityRepository.saveForTeacher(any(AvailabilitySlot.class), anyLong()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<AvailabilitySlot> result = availabilityService.createAvailability(request);

        assertNotNull(result);
        assertEquals(4, result.size());

        assertEquals(LocalDate.of(2026, 3, 12), result.get(0).getDate());
        assertEquals(LocalTime.of(10, 0), result.get(0).getStartTime());
        assertEquals(LocalTime.of(10, 30), result.get(0).getEndTime());
        assertFalse(result.get(0).isBooked());

        assertEquals(LocalTime.of(10, 30), result.get(1).getStartTime());
        assertEquals(LocalTime.of(11, 0), result.get(1).getEndTime());
        assertFalse(result.get(1).isBooked());

        assertEquals(LocalTime.of(11, 0), result.get(2).getStartTime());
        assertEquals(LocalTime.of(11, 30), result.get(2).getEndTime());
        assertFalse(result.get(2).isBooked());

        assertEquals(LocalTime.of(11, 30), result.get(3).getStartTime());
        assertEquals(LocalTime.of(12, 0), result.get(3).getEndTime());
        assertFalse(result.get(3).isBooked());

        verify(availabilityRepository, times(4))
                .saveForTeacher(any(AvailabilitySlot.class), eq(1L));
    }

    @Test
    void createAvailability_startAfterEnd_throwsException() {
        CreateAvailabilityRequest request = new CreateAvailabilityRequest();
        request.setTeacherId(1L);
        request.setDate(LocalDate.of(2026, 3, 12));
        request.setStartTime(LocalTime.of(12, 0));
        request.setEndTime(LocalTime.of(10, 0));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> availabilityService.createAvailability(request)
        );

        assertEquals("Start time must be before end time", exception.getMessage());

        verify(availabilityRepository, never())
                .saveForTeacher(any(AvailabilitySlot.class), anyLong());
    }

    @Test
    void createAvailability_startEqualsEnd_throwsException() {
        CreateAvailabilityRequest request = new CreateAvailabilityRequest();
        request.setTeacherId(1L);
        request.setDate(LocalDate.of(2026, 3, 12));
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(10, 0));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> availabilityService.createAvailability(request)
        );

        assertEquals("Start time must be before end time", exception.getMessage());

        verify(availabilityRepository, never())
                .saveForTeacher(any(AvailabilitySlot.class), anyLong());
    }

    @Test
    void createAvailability_timeRangeNotDivisibleByThirty_throwsException() {
        CreateAvailabilityRequest request = new CreateAvailabilityRequest();
        request.setTeacherId(1L);
        request.setDate(LocalDate.of(2026, 3, 12));
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(10, 45));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> availabilityService.createAvailability(request)
        );

        assertEquals("Time range must be divisible into 30-minute slots", exception.getMessage());

        verify(availabilityRepository, never())
                .saveForTeacher(any(AvailabilitySlot.class), anyLong());
    }

    @Test
    void getTeacherAvailability_valid_returnsAvailabilitySlotResponses() {
        List<AvailabilitySlotEntity> slotEntities = List.of(
                new AvailabilitySlotEntity(
                        1L,
                        1L,
                        LocalDate.of(2026, 3, 12),
                        LocalTime.of(10, 0),
                        LocalTime.of(10, 30),
                        false
                )
        );

        when(availabilityRepository.findEntitiesByTeacherId(1L))
                .thenReturn(slotEntities);

        List<AvailabilitySlotResponse> result =
                availabilityService.getTeacherAvailability(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals(LocalDate.of(2026, 3, 12), result.get(0).getDate());
        assertEquals(LocalTime.of(10, 0), result.get(0).getStartTime());
        assertEquals(LocalTime.of(10, 30), result.get(0).getEndTime());
        assertFalse(result.get(0).isBooked());

        verify(availabilityRepository).findEntitiesByTeacherId(1L);
    }
}