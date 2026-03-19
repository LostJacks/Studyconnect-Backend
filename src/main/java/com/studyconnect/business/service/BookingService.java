package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IBookingRepository;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.presentation.dto.CreateBookingRequest;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final IBookingRepository bookingRepository;

    public BookingService(IBookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public BookingRequest createBooking(CreateBookingRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        if (request.getSlotId() == null ||
                request.getRequesterId() == null ||
                request.getStartTime() == null ||
                request.getEndTime() == null ||
                request.getTitle() == null || request.getTitle().isBlank() ||
                request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("All fields must be filled in");
        }

        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        BookingRequest bookingRequest = new BookingRequest(
                null,
                request.getSlotId(),
                request.getRequesterId(),
                request.getStartTime(),
                request.getEndTime(),
                request.getTitle(),
                request.getDescription()
        );

        return bookingRepository.save(bookingRequest);
    }
}