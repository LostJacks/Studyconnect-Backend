package com.studyconnect.data.irepository;

import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;

public interface IBookingRepository {

    BookingRequest saveForSlotAndRequester(BookingRequest bookingRequest, Long slotId, Long requesterId);

    BookingRequest findById(Long bookingId);

    Long findSlotIdByBookingId(Long bookingId);

    void updateStatus(Long bookingId, BookingStatus status);
}