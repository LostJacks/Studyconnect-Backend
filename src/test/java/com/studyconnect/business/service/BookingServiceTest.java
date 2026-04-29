package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.data.irepository.IBookingRepository;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;
import com.studyconnect.domain.model.Role;
import com.studyconnect.domain.model.User;
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
    private IUserRepository userRepository;
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(IBookingRepository.class);
        availabilityRepository = mock(IAvailabilityRepository.class);
        userRepository = mock(IUserRepository.class);
        bookingService = new BookingService(bookingRepository, availabilityRepository, userRepository);
    }

    @Test
    void createBooking_validRequest_returnsBooking() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(1L);
        request.setRequesterId(2L);
        request.setTitle("Help with math");
        request.setDescription("Need help with algebra");

        User requester = new User(
                "Student User",
                "student@test.com",
                "encodedPassword",
                Role.STUDENT
        );

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

        when(userRepository.findById(2L)).thenReturn(requester);
        when(availabilityRepository.findById(1L)).thenReturn(slot);
        when(bookingRepository.saveForSlotAndRequester(any(BookingRequest.class), eq(1L), eq(2L)))
                .thenReturn(savedBooking);

        BookingRequest result = bookingService.createBooking(request);

        assertNotNull(result);
        assertEquals(BookingStatus.PENDING, result.getStatus());

        verify(userRepository).findById(2L);
        verify(availabilityRepository).findById(1L);
        verify(bookingRepository).saveForSlotAndRequester(any(BookingRequest.class), eq(1L), eq(2L));
    }

    @Test
    void createBooking_requesterDoesNotExist_throwsException() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(1L);
        request.setRequesterId(99L);
        request.setTitle("Test");
        request.setDescription("Test");

        when(userRepository.findById(99L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(request)
        );

        assertNotNull(exception.getMessage());

        verify(userRepository).findById(99L);
        verifyNoInteractions(availabilityRepository);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBooking_requesterIsTeacher_throwsException() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(1L);
        request.setRequesterId(2L);
        request.setTitle("Test");
        request.setDescription("Test");

        User requester = new User(
                "Teacher User",
                "teacher@test.com",
                "encodedPassword",
                Role.TEACHER
        );

        when(userRepository.findById(2L)).thenReturn(requester);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(request)
        );

        assertEquals("Only students can create booking requests", exception.getMessage());

        verify(userRepository).findById(2L);
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

        User requester = new User(
                "Student User",
                "student@test.com",
                "encodedPassword",
                Role.STUDENT
        );

        when(userRepository.findById(2L)).thenReturn(requester);
        when(availabilityRepository.findById(99L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(request)
        );

        assertEquals("Availability slot does not exist", exception.getMessage());

        verify(userRepository).findById(2L);
        verify(availabilityRepository).findById(99L);
        verifyNoInteractions(bookingRepository);
    }

    @Test
    void createBooking_slotAlreadyBooked_throwsException() {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setSlotId(1L);
        request.setRequesterId(2L);
        request.setTitle("Test");
        request.setDescription("Test");

        User requester = new User(
                "Student User",
                "student@test.com",
                "encodedPassword",
                Role.STUDENT
        );

        AvailabilitySlot slot = new AvailabilitySlot(
                LocalDate.now(),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                true
        );

        when(userRepository.findById(2L)).thenReturn(requester);
        when(availabilityRepository.findById(1L)).thenReturn(slot);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.createBooking(request)
        );

        assertEquals("This slot is already booked", exception.getMessage());

        verify(userRepository).findById(2L);
        verify(availabilityRepository).findById(1L);
        verifyNoInteractions(bookingRepository);
    }
}