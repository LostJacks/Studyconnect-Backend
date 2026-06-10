package com.studyconnect.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingRequestTest {

    private static final LocalTime START_TIME = LocalTime.of(10, 0);
    private static final LocalTime END_TIME = LocalTime.of(10, 30);
    private static final String TITLE = "Math help";
    private static final String DESCRIPTION = "Need help with algebra";

    @Test
    void constructor_validData_setsPendingStatus() {
        BookingRequest booking = createPendingBooking();

        assertEquals(BookingStatus.PENDING, booking.getStatus());
        assertTrue(booking.isPending());
    }

    @Test
    void constructor_nullStartTime_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createBooking(null, END_TIME, TITLE, DESCRIPTION)
        );

        assertEquals("Start time and end time are required", exception.getMessage());
    }

    @Test
    void constructor_nullEndTime_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createBooking(START_TIME, null, TITLE, DESCRIPTION)
        );

        assertEquals("Start time and end time are required", exception.getMessage());
    }

    @Test
    void constructor_startAfterEnd_throwsException() {
        LocalTime invalidStart = LocalTime.of(11, 0);
        LocalTime invalidEnd = LocalTime.of(10, 0);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createBooking(invalidStart, invalidEnd, TITLE, DESCRIPTION)
        );

        assertEquals("Start time must be before end time", exception.getMessage());
    }

    @Test
    void constructor_emptyTitle_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createBooking(START_TIME, END_TIME, "", DESCRIPTION)
        );

        assertEquals("Title cannot be empty", exception.getMessage());
    }

    @Test
    void constructor_nullTitle_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createBooking(START_TIME, END_TIME, null, DESCRIPTION)
        );

        assertEquals("Title cannot be empty", exception.getMessage());
    }

    @Test
    void constructor_emptyDescription_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createBooking(START_TIME, END_TIME, TITLE, "")
        );

        assertEquals("Description cannot be empty", exception.getMessage());
    }

    @Test
    void constructor_nullDescription_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createBooking(START_TIME, END_TIME, TITLE, null)
        );

        assertEquals("Description cannot be empty", exception.getMessage());
    }

    @Test
    void constructor_nullStatus_throwsException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> createBookingWithStatus(null)
        );

        assertEquals("Booking status cannot be null", exception.getMessage());
    }

    @Test
    void accept_pendingBooking_changesStatusToAccepted() {
        BookingRequest booking = createPendingBooking();

        booking.accept();

        assertEquals(BookingStatus.ACCEPTED, booking.getStatus());
    }

    @Test
    void accept_nonPendingBooking_throwsException() {
        BookingRequest booking = createBookingWithStatus(BookingStatus.REJECTED);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                booking::accept
        );

        assertEquals("Only pending bookings can be accepted", exception.getMessage());
    }

    @Test
    void reject_pendingBooking_changesStatusToRejected() {
        BookingRequest booking = createPendingBooking();

        booking.reject();

        assertEquals(BookingStatus.REJECTED, booking.getStatus());
    }

    @Test
    void reject_nonPendingBooking_throwsException() {
        BookingRequest booking = createBookingWithStatus(BookingStatus.ACCEPTED);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                booking::reject
        );

        assertEquals("Only pending bookings can be rejected", exception.getMessage());
    }

    @Test
    void cancel_validBooking_changesStatusToCancelled() {
        BookingRequest booking = createPendingBooking();

        booking.cancel();

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
    }

    @Test
    void cancel_completedBooking_throwsException() {
        BookingRequest booking = createBookingWithStatus(BookingStatus.COMPLETED);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                booking::cancel
        );

        assertEquals("Completed bookings cannot be cancelled", exception.getMessage());
    }

    private BookingRequest createPendingBooking() {
        return createBooking(START_TIME, END_TIME, TITLE, DESCRIPTION);
    }

    private BookingRequest createBooking(
            LocalTime startTime,
            LocalTime endTime,
            String title,
            String description
    ) {
        return new BookingRequest(startTime, endTime, title, description);
    }

    private BookingRequest createBookingWithStatus(BookingStatus status) {
        return new BookingRequest(
                START_TIME,
                END_TIME,
                TITLE,
                DESCRIPTION,
                status
        );
    }
}