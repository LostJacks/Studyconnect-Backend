package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.data.irepository.IBookingRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.presentation.dto.CreateBookingRequest;
import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final IBookingRepository bookingRepository;
    private final IAvailabilityRepository availabilityRepository;

    public BookingService(IBookingRepository bookingRepository,
                          IAvailabilityRepository availabilityRepository) {
        this.bookingRepository = bookingRepository;
        this.availabilityRepository = availabilityRepository;
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

        AvailabilitySlot slot = availabilityRepository.findById(request.getSlotId());

        if (slot == null) {
            throw new IllegalArgumentException("Availability slot does not exist");
        }

        if (request.getStartTime().isBefore(slot.getStartTime()) ||
                request.getEndTime().isAfter(slot.getEndTime())) {
            throw new IllegalArgumentException("Requested time is outside the helper's availability");
        }

        boolean hasOverlap = bookingRepository.existsOverlappingBooking(
                request.getSlotId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (hasOverlap) {
            throw new IllegalArgumentException("This time range is already booked");
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