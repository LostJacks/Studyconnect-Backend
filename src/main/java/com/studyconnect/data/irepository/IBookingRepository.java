package com.studyconnect.data.irepository;

import com.studyconnect.domain.model.BookingRequest;

public interface IBookingRepository {
    BookingRequest save(BookingRequest bookingRequest);
}