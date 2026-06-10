package com.studyconnect.business.service;

import com.studyconnect.data.entity.AvailabilitySlotEntity;
import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.presentation.dto.CreateAvailabilityRequest;
import com.studyconnect.presentation.dto.AvailabilitySlotResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
        assertTrue(result.get(0).isAvailable());

        assertEquals(LocalTime.of(10, 30), result.get(1).getStartTime());
        assertEquals(LocalTime.of(11, 0), result.get(1).getEndTime());
        assertFalse(result.get(1).isBooked());
        assertTrue(result.get(1).isAvailable());

        assertEquals(LocalTime.of(11, 0), result.get(2).getStartTime());
        assertEquals(LocalTime.of(11, 30), result.get(2).getEndTime());
        assertFalse(result.get(2).isBooked());
        assertTrue(result.get(2).isAvailable());

        assertEquals(LocalTime.of(11, 30), result.get(3).getStartTime());
        assertEquals(LocalTime.of(12, 0), result.get(3).getEndTime());
        assertFalse(result.get(3).isBooked());
        assertTrue(result.get(3).isAvailable());

        verify(availabilityRepository, times(4))
                .saveForTeacher(any(AvailabilitySlot.class), eq(1L));
    }

    @ParameterizedTest
    @CsvSource({
            "12:00,10:00,Start time must be before end time",
            "10:00,10:00,Start time must be before end time",
            "10:00,10:45,Time range must be divisible into 30-minute slots"
    })
    void createAvailability_invalidTimeRange_throwsException(
            String startTime,
            String endTime,
            String expectedMessage
    ) {
        CreateAvailabilityRequest request = new CreateAvailabilityRequest();
        request.setTeacherId(1L);
        request.setDate(LocalDate.of(2026, 3, 12));
        request.setStartTime(LocalTime.parse(startTime));
        request.setEndTime(LocalTime.parse(endTime));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> availabilityService.createAvailability(request)
        );

        assertEquals(expectedMessage, exception.getMessage());

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
                        false,
                        true
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
        assertTrue(result.get(0).isAvailable());

        verify(availabilityRepository).findEntitiesByTeacherId(1L);
    }
}