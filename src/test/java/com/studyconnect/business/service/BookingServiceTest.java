package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.data.irepository.IBookingRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;
import com.studyconnect.presentation.dto.CreateBookingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    private IBookingRepository bookingRepository;
    private IAvailabilityRepository availabilityRepository;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(IBookingRepository.class);
        availabilityRepository = mock(IAvailabilityRepository.class);
        bookingService = new BookingService(bookingRepository, availabilityRepository);
    }

    @Test
    void createBooking_validRequest_returnsBooking() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(1L);
        request.setRequesterId(2L);
        request.setTitle("Help with math");
        request.setDescription("Need help with algebra");

        AvailabilitySlot slot = new AvailabilitySlot(
                LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                false
        );

        BookingRequest savedBooking = new BookingRequest(
                slot.getStartTime(),
                slot.getEndTime(),
                request.getTitle(),
                request.getDescription(),
                BookingStatus.PENDING
        );

        when(availabilityRepository.findById(1L)).thenReturn(slot);
        when(bookingRepository.saveForSlotAndRequester(any(BookingRequest.class), eq(1L), eq(2L)))
                .thenReturn(savedBooking);

        BookingRequest result = bookingService.createBooking(request);

        assertNotNull(result);
        assertEquals(BookingStatus.PENDING, result.getStatus());

        verify(availabilityRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1))
                .saveForSlotAndRequester(any(BookingRequest.class), eq(1L), eq(2L));
    }

    @Test
    void createBooking_nullRequest_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(null)
        );

        assertEquals("Request cannot be null", exception.getMessage());
        verifyNoInteractions(availabilityRepository);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBooking_missingField_throwsException() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(1L);
        request.setRequesterId(2L);
        request.setTitle("");
        request.setDescription("Test");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(request)
        );

        assertEquals("All fields must be filled in", exception.getMessage());
        verifyNoInteractions(availabilityRepository);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBooking_slotDoesNotExist_throwsException() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(99L);
        request.setRequesterId(2L);
        request.setTitle("Test");
        request.setDescription("Test");

        when(availabilityRepository.findById(99L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(request)
        );

        assertEquals("Availability slot does not exist", exception.getMessage());
        verify(availabilityRepository, times(1)).findById(99L);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBooking_slotAlreadyBooked_throwsException() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(1L);
        request.setRequesterId(2L);
        request.setTitle("Test");
        request.setDescription("Test");

        AvailabilitySlot slot = new AvailabilitySlot(
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                true
        );

        when(availabilityRepository.findById(1L)).thenReturn(slot);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(request)
        );

        assertEquals("This slot is already booked", exception.getMessage());
        verify(availabilityRepository, times(1)).findById(1L);
        verifyNoInteractions(bookingRepository);
    }
}