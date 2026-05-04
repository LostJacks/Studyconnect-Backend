package com.studyconnect.business.service;

import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;
import com.studyconnect.presentation.dto.BookingSessionResponse;
import com.studyconnect.presentation.dto.CreateBookingRequest;

import java.util.List;

public interface IBookingService {
    BookingRequest createBooking(CreateBookingRequest request);

    BookingRequest updateBookingStatus(Long bookingId, Long teacherId, BookingStatus status);

    List<BookingSessionResponse> getBookingsByUser(Long userId);
}