package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.data.irepository.IBookingRepository;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;
import com.studyconnect.domain.model.Role;
import com.studyconnect.domain.model.User;
import com.studyconnect.presentation.dto.BookingSessionResponse;
import com.studyconnect.presentation.dto.CreateBookingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

        bookingService = new BookingService(
                bookingRepository,
                availabilityRepository,
                userRepository
        );
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
                request.getDescription()
        );

        when(userRepository.findById(2L)).thenReturn(requester);
        when(availabilityRepository.findById(1L)).thenReturn(slot);
        when(bookingRepository.saveForSlotAndRequester(
                any(BookingRequest.class),
                eq(1L),
                eq(2L)
        )).thenReturn(savedBooking);

        BookingRequest result = bookingService.createBooking(request);

        assertNotNull(result);
        assertEquals(LocalTime.of(10, 0), result.getStartTime());
        assertEquals(LocalTime.of(10, 30), result.getEndTime());
        assertEquals("Help with math", result.getTitle());
        assertEquals("Need help with algebra", result.getDescription());
        assertEquals(BookingStatus.PENDING, result.getStatus());

        verify(userRepository).findById(2L);
        verify(availabilityRepository).findById(1L);
        verify(bookingRepository).saveForSlotAndRequester(
                any(BookingRequest.class),
                eq(1L),
                eq(2L)
        );
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

        assertEquals("Student does not exist", exception.getMessage());

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
                LocalDate.of(2026, 3, 20),
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

    @Test
    void cancelBooking_validBooking_returnsCancelledBooking() {
        BookingRequest bookingRequest = new BookingRequest(
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                "Help with math",
                "Need help"
        );

        when(bookingRepository.findById(1L)).thenReturn(bookingRequest);

        BookingRequest result = bookingService.cancelBooking(1L);

        assertNotNull(result);
        assertEquals(BookingStatus.CANCELLED, result.getStatus());

        verify(bookingRepository).findById(1L);
        verify(bookingRepository).updateStatus(1L, BookingStatus.CANCELLED);
    }

    @Test
    void cancelBooking_bookingNotFound_throwsException() {
        when(bookingRepository.findById(99L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.cancelBooking(99L)
        );

        assertEquals("Booking request not found", exception.getMessage());

        verify(bookingRepository).findById(99L);
        verify(bookingRepository, never()).updateStatus(any(), any());
    }

    @Test
    void updateBookingStatus_acceptValidBooking_returnsAcceptedBooking() {
        BookingRequest bookingRequest = new BookingRequest(
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                "Help with math",
                "Need help"
        );

        AvailabilitySlot slot = new AvailabilitySlot(
                LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                false
        );

        when(bookingRepository.findById(1L)).thenReturn(bookingRequest);
        when(bookingRepository.findSlotIdByBookingId(1L)).thenReturn(5L);
        when(availabilityRepository.findById(5L)).thenReturn(slot);
        when(availabilityRepository.findTeacherIdBySlotId(5L)).thenReturn(10L);

        BookingRequest result = bookingService.updateBookingStatus(
                1L,
                10L,
                BookingStatus.ACCEPTED
        );

        assertNotNull(result);
        assertEquals(BookingStatus.ACCEPTED, result.getStatus());

        verify(bookingRepository).findById(1L);
        verify(bookingRepository).findSlotIdByBookingId(1L);
        verify(availabilityRepository).findById(5L);
        verify(availabilityRepository).findTeacherIdBySlotId(5L);
        verify(availabilityRepository).updateBookedStatus(5L, true);
        verify(bookingRepository).updateStatus(1L, BookingStatus.ACCEPTED);
    }

    @Test
    void updateBookingStatus_rejectValidBooking_returnsRejectedBooking() {
        BookingRequest bookingRequest = new BookingRequest(
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                "Help with math",
                "Need help"
        );

        AvailabilitySlot slot = new AvailabilitySlot(
                LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                false
        );

        when(bookingRepository.findById(1L)).thenReturn(bookingRequest);
        when(bookingRepository.findSlotIdByBookingId(1L)).thenReturn(5L);
        when(availabilityRepository.findById(5L)).thenReturn(slot);
        when(availabilityRepository.findTeacherIdBySlotId(5L)).thenReturn(10L);

        BookingRequest result = bookingService.updateBookingStatus(
                1L,
                10L,
                BookingStatus.REJECTED
        );

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());

        verify(availabilityRepository).updateBookedStatus(5L, false);
        verify(bookingRepository).updateStatus(1L, BookingStatus.REJECTED);
    }

    @Test
    void updateBookingStatus_bookingNotFound_throwsException() {
        when(bookingRepository.findById(99L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.updateBookingStatus(99L, 10L, BookingStatus.ACCEPTED)
        );

        assertEquals("Booking request not found", exception.getMessage());

        verify(bookingRepository).findById(99L);
        verifyNoInteractions(availabilityRepository);
    }

    @Test
    void updateBookingStatus_bookingNotPending_throwsException() {
        BookingRequest bookingRequest = new BookingRequest(
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                "Help with math",
                "Need help"
        );

        bookingRequest.accept();

        when(bookingRepository.findById(1L)).thenReturn(bookingRequest);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.updateBookingStatus(1L, 10L, BookingStatus.ACCEPTED)
        );

        assertEquals("Only pending booking requests can be updated", exception.getMessage());

        verify(bookingRepository).findById(1L);
        verify(bookingRepository, never()).findSlotIdByBookingId(any());
        verifyNoInteractions(availabilityRepository);
    }

    @Test
    void updateBookingStatus_teacherIsNotTargetedTeacher_throwsException() {
        BookingRequest bookingRequest = new BookingRequest(
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                "Help with math",
                "Need help"
        );

        AvailabilitySlot slot = new AvailabilitySlot(
                LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                false
        );

        when(bookingRepository.findById(1L)).thenReturn(bookingRequest);
        when(bookingRepository.findSlotIdByBookingId(1L)).thenReturn(5L);
        when(availabilityRepository.findById(5L)).thenReturn(slot);
        when(availabilityRepository.findTeacherIdBySlotId(5L)).thenReturn(20L);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.updateBookingStatus(1L, 10L, BookingStatus.ACCEPTED)
        );

        assertEquals(
                "Only the targeted teacher can respond to this booking request",
                exception.getMessage()
        );

        verify(availabilityRepository, never()).updateBookedStatus(any(), anyBoolean());
        verify(bookingRepository, never()).updateStatus(any(), any());
    }

    @Test
    void updateBookingStatus_acceptAlreadyBookedSlot_throwsException() {
        BookingRequest bookingRequest = new BookingRequest(
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                "Help with math",
                "Need help"
        );

        AvailabilitySlot slot = new AvailabilitySlot(
                LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                true
        );

        when(bookingRepository.findById(1L)).thenReturn(bookingRequest);
        when(bookingRepository.findSlotIdByBookingId(1L)).thenReturn(5L);
        when(availabilityRepository.findById(5L)).thenReturn(slot);
        when(availabilityRepository.findTeacherIdBySlotId(5L)).thenReturn(10L);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.updateBookingStatus(1L, 10L, BookingStatus.ACCEPTED)
        );

        assertEquals("This slot is already booked", exception.getMessage());

        verify(availabilityRepository, never()).updateBookedStatus(any(), anyBoolean());
        verify(bookingRepository, never()).updateStatus(any(), any());
    }

    @Test
    void updateBookingStatus_invalidStatus_throwsException() {
        BookingRequest bookingRequest = new BookingRequest(
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                "Help with math",
                "Need help"
        );

        AvailabilitySlot slot = new AvailabilitySlot(
                LocalDate.of(2026, 3, 20),
                LocalTime.of(10, 0),
                LocalTime.of(10, 30),
                false
        );

        when(bookingRepository.findById(1L)).thenReturn(bookingRequest);
        when(bookingRepository.findSlotIdByBookingId(1L)).thenReturn(5L);
        when(availabilityRepository.findById(5L)).thenReturn(slot);
        when(availabilityRepository.findTeacherIdBySlotId(5L)).thenReturn(10L);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.updateBookingStatus(1L, 10L, BookingStatus.CANCELLED)
        );

        assertEquals("Status must be ACCEPTED or REJECTED", exception.getMessage());

        verify(availabilityRepository, never()).updateBookedStatus(any(), anyBoolean());
        verify(bookingRepository, never()).updateStatus(any(), any());
    }

    @Test
    void getBookingsByUser_userDoesNotExist_throwsException() {
        when(userRepository.findById(99L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> bookingService.getBookingsByUser(99L)
        );

        assertEquals("User does not exist", exception.getMessage());

        verify(userRepository).findById(99L);
        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(availabilityRepository);
    }
}