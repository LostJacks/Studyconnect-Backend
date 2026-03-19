package com.studyconnect.data.irepository;

import com.studyconnect.domain.model.BookingRequest;

import java.time.LocalTime;

public interface IBookingRepository {

    BookingRequest save(BookingRequest bookingRequest);

    boolean existsOverlappingBooking(Long slotId, LocalTime startTime, LocalTime endTime);
}