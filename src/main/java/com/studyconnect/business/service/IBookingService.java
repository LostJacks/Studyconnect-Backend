package com.studyconnect.business.service;

import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;
import com.studyconnect.presentation.dto.CreateBookingRequest;

public interface IBookingService {
    BookingRequest createBooking(CreateBookingRequest request);
    BookingRequest updateBookingStatus(Long bookingId, Long teacherId, BookingStatus status);
}