package com.studyconnect.data.irepository;

import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;

public interface IBookingRepository {

    BookingRequest save(BookingRequest bookingRequest);

    BookingRequest findById(Long bookingId);

    void updateStatus(Long bookingId, BookingStatus status);
}