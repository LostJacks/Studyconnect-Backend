package com.studyconnect.data.irepository;

import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;
import com.studyconnect.presentation.dto.BookingSessionResponse;

import java.util.List;

public interface IBookingRepository {

    BookingRequest saveForSlotAndRequester(
            BookingRequest bookingRequest,
            Long slotId,
            Long requesterId
    );

    BookingRequest findById(Long bookingId);

    Long findSlotIdByBookingId(Long bookingId);

    void updateStatus(Long bookingId, BookingStatus status);

    List<BookingSessionResponse> findBookingsByUserId(Long userId);
}