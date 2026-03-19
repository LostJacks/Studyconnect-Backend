package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.data.irepository.IBookingRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.presentation.dto.CreateBookingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(12, 0));
        request.setTitle("Help with math");
        request.setDescription("Need help with algebra");

        AvailabilitySlot slot = new AvailabilitySlot(
                1L,
                10L,
                LocalDate.of(2026, 3, 20),
                LocalTime.of(9, 0),
                LocalTime.of(13, 0),
                false
        );

        BookingRequest savedBooking = new BookingRequest(
                1L,
                1L,
                2L,
                request.getStartTime(),
                request.getEndTime(),
                request.getTitle(),
                request.getDescription()
        );

        when(availabilityRepository.findById(1L)).thenReturn(slot);
        when(bookingRepository.existsOverlappingBooking(
                1L,
                LocalTime.of(10, 0),
                LocalTime.of(12, 0)
        )).thenReturn(false);
        when(bookingRepository.save(any(BookingRequest.class))).thenReturn(savedBooking);

        BookingRequest result = bookingService.createBooking(request);

        assertNotNull(result);
        assertEquals(2L, result.getRequesterId());
        assertEquals("Help with math", result.getTitle());
        verify(availabilityRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1))
                .existsOverlappingBooking(1L, LocalTime.of(10, 0), LocalTime.of(12, 0));
        verify(bookingRepository, times(1)).save(any(BookingRequest.class));
    }

    @Test
    void createBooking_nullRequest_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(null)
        );

        assertEquals("Request cannot be null", exception.getMessage());
        verifyNoInteractions(availabilityRepository);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_missingField_throwsException() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(1L);
        request.setRequesterId(2L);
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(12, 0));
        request.setTitle("");
        request.setDescription("Need help");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(request)
        );

        assertEquals("All fields must be filled in", exception.getMessage());
        verifyNoInteractions(availabilityRepository);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_invalidTime_throwsException() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(1L);
        request.setRequesterId(2L);
        request.setStartTime(LocalTime.of(12, 0));
        request.setEndTime(LocalTime.of(10, 0));
        request.setTitle("Test");
        request.setDescription("Test");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(request)
        );

        assertEquals("Start time must be before end time", exception.getMessage());
        verifyNoInteractions(availabilityRepository);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_slotDoesNotExist_throwsException() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(99L);
        request.setRequesterId(2L);
        request.setStartTime(LocalTime.of(10, 0));
        request.setEndTime(LocalTime.of(11, 0));
        request.setTitle("Java help");
        request.setDescription("Need help with interfaces");

        when(availabilityRepository.findById(99L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(request)
        );

        assertEquals("Availability slot does not exist", exception.getMessage());
        verify(availabilityRepository, times(1)).findById(99L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_timeOutsideAvailability_throwsException() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(1L);
        request.setRequesterId(2L);
        request.setStartTime(LocalTime.of(8, 0));
        request.setEndTime(LocalTime.of(10, 0));
        request.setTitle("Java help");
        request.setDescription("Need help with abstract classes");

        AvailabilitySlot slot = new AvailabilitySlot(
                1L,
                10L,
                LocalDate.of(2026, 3, 20),
                LocalTime.of(9, 0),
                LocalTime.of(13, 0),
                false
        );

        when(availabilityRepository.findById(1L)).thenReturn(slot);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(request)
        );

        assertEquals("Requested time is outside the helper's availability", exception.getMessage());
        verify(availabilityRepository, times(1)).findById(1L);
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_overlappingBooking_throwsException() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(1L);
        request.setRequesterId(2L);
        request.setStartTime(LocalTime.of(10, 30));
        request.setEndTime(LocalTime.of(11, 30));
        request.setTitle("Test");
        request.setDescription("Test overlap");

        AvailabilitySlot slot = new AvailabilitySlot(
                1L,
                10L,
                LocalDate.of(2026, 3, 20),
                LocalTime.of(9, 0),
                LocalTime.of(13, 0),
                false
        );

        when(availabilityRepository.findById(1L)).thenReturn(slot);
        when(bookingRepository.existsOverlappingBooking(
                1L,
                LocalTime.of(10, 30),
                LocalTime.of(11, 30)
        )).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(request)
        );

        assertEquals("This time range is already booked", exception.getMessage());
        verify(availabilityRepository, times(1)).findById(1L);
        verify(bookingRepository, times(1))
                .existsOverlappingBooking(1L, LocalTime.of(10, 30), LocalTime.of(11, 30));
        verify(bookingRepository, never()).save(any());
    }
}