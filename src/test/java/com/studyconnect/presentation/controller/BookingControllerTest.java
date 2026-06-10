package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.BookingService;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;
import com.studyconnect.presentation.dto.BookingSessionResponse;
import com.studyconnect.presentation.dto.CreateBookingRequest;
import com.studyconnect.presentation.dto.UpdateBookingStatusRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingControllerTest {

    private final BookingService bookingService =
            mock(BookingService.class);

    private final BookingController bookingController =
            new BookingController(bookingService);

    @Test
    void createBooking_returnsBooking() {
        CreateBookingRequest request = mock(CreateBookingRequest.class);

        BookingRequest booking =
                new BookingRequest(
                        LocalTime.of(10, 0),
                        LocalTime.of(10, 30),
                        "Java Help",
                        "Need help with Spring"
                );

        when(bookingService.createBooking(request))
                .thenReturn(booking);

        BookingRequest result =
                bookingController.createBooking(request);

        assertSame(booking, result);

        verify(bookingService).createBooking(request);
    }

    @Test
    void updateBookingStatus_returnsUpdatedBooking() {
        UpdateBookingStatusRequest request =
                new UpdateBookingStatusRequest();

        request.setTeacherId(5L);
        request.setStatus(BookingStatus.ACCEPTED);

        BookingRequest booking =
                new BookingRequest(
                        LocalTime.of(10, 0),
                        LocalTime.of(10, 30),
                        "Java Help",
                        "Need help with Spring"
                );

        when(bookingService.updateBookingStatus(
                1L,
                5L,
                BookingStatus.ACCEPTED
        )).thenReturn(booking);

        BookingRequest result =
                bookingController.updateBookingStatus(
                        1L,
                        request
                );

        assertSame(booking, result);

        verify(bookingService)
                .updateBookingStatus(
                        1L,
                        5L,
                        BookingStatus.ACCEPTED
                );
    }

    @Test
    void cancelBooking_returnsOkResponse() {
        BookingRequest booking =
                new BookingRequest(
                        LocalTime.of(10, 0),
                        LocalTime.of(10, 30),
                        "Java Help",
                        "Need help with Spring"
                );

        when(bookingService.cancelBooking(1L))
                .thenReturn(booking);

        ResponseEntity<BookingRequest> result =
                bookingController.cancelBooking(1L);

        assertEquals(200, result.getStatusCode().value());
        assertSame(booking, result.getBody());

        verify(bookingService).cancelBooking(1L);
    }

    @Test
    void getBookingsByUser_returnsBookings() {
        List<BookingSessionResponse> sessions = List.of(
                mock(BookingSessionResponse.class)
        );

        when(bookingService.getBookingsByUser(1L))
                .thenReturn(sessions);

        List<BookingSessionResponse> result =
                bookingController.getBookingsByUser(1L);

        assertSame(sessions, result);

        verify(bookingService).getBookingsByUser(1L);
    }
}