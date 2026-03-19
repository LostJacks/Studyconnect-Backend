package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IBookingRepository;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.presentation.dto.CreateBookingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class BookingServiceTest {

    private IBookingRepository bookingRepository;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(IBookingRepository.class);
        bookingService = new BookingService(bookingRepository);
    }

    @Test
    void createBooking_validRequest_returnsBooking() {

        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(1L);
        request.setRequesterId(2L);
        request.setStartTime(LocalTime.of(10,0));
        request.setEndTime(LocalTime.of(12,0));
        request.setTitle("Help with math");
        request.setDescription("Need help with algebra");

        BookingRequest saved = new BookingRequest(
                1L,
                1L,
                2L,
                request.getStartTime(),
                request.getEndTime(),
                request.getTitle(),
                request.getDescription()
        );

        when(bookingRepository.save(any())).thenReturn(saved);

        BookingRequest result = bookingService.createBooking(request);

        assertNotNull(result);
        assertEquals(2L, result.getRequesterId());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    void createBooking_nullRequest_throwsException() {

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.createBooking(null)
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_missingField_throwsException() {

        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(1L);
        request.setRequesterId(2L);
        request.setStartTime(LocalTime.of(10,0));
        request.setEndTime(LocalTime.of(12,0));
        request.setTitle(""); // invalid
        request.setDescription("desc");

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.createBooking(request)
        );

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_invalidTime_throwsException() {

        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(1L);
        request.setRequesterId(2L);
        request.setStartTime(LocalTime.of(12,0));
        request.setEndTime(LocalTime.of(10,0));
        request.setTitle("Test");
        request.setDescription("Test");

        assertThrows(IllegalArgumentException.class, () ->
                bookingService.createBooking(request)
        );

        verify(bookingRepository, never()).save(any());
    }
}