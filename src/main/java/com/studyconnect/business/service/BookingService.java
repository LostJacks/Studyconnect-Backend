package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.data.irepository.IBookingRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;
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
                request.getTitle() == null || request.getTitle().isBlank() ||
                request.getDescription() == null || request.getDescription().isBlank()) {
            throw new IllegalArgumentException("All fields must be filled in");
        }

        AvailabilitySlot slot = availabilityRepository.findById(request.getSlotId());

        if (slot == null) {
            throw new IllegalArgumentException("Availability slot does not exist");
        }

        if (slot.isBooked()) {
            throw new IllegalArgumentException("This slot is already booked");
        }

        BookingRequest bookingRequest = new BookingRequest(
                null,
                request.getSlotId(),
                request.getRequesterId(),
                slot.getStartTime(),
                slot.getEndTime(),
                request.getTitle(),
                request.getDescription(),
                BookingStatus.PENDING
        );

        return bookingRepository.save(bookingRequest);
    }

    public BookingRequest acceptBooking(Long bookingId, Long teacherId) {

        if (bookingId == null) {
            throw new IllegalArgumentException("BookingId cannot be null");
        }

        if (teacherId == null) {
            throw new IllegalArgumentException("TeacherId cannot be null");
        }

        BookingRequest bookingRequest = bookingRepository.findById(bookingId);

        if (bookingRequest == null) {
            throw new IllegalArgumentException("Booking request not found");
        }

        if (bookingRequest.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only pending booking requests can be accepted");
        }

        AvailabilitySlot slot = availabilityRepository.findById(bookingRequest.getSlotId());

        if (slot == null) {
            throw new IllegalArgumentException("Availability slot does not exist");
        }

        if (!slot.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException("Only the targeted teacher can respond to this booking request");
        }

        if (slot.isBooked()) {
            throw new IllegalArgumentException("This slot is already booked");
        }

        bookingRepository.updateStatus(bookingId, BookingStatus.ACCEPTED);
        availabilityRepository.updateBookedStatus(slot.getId(), true);

        return new BookingRequest(
                bookingRequest.getId(),
                bookingRequest.getSlotId(),
                bookingRequest.getRequesterId(),
                bookingRequest.getStartTime(),
                bookingRequest.getEndTime(),
                bookingRequest.getTitle(),
                bookingRequest.getDescription(),
                BookingStatus.ACCEPTED
        );
    }

    public BookingRequest rejectBooking(Long bookingId, Long teacherId) {

        if (bookingId == null) {
            throw new IllegalArgumentException("BookingId cannot be null");
        }

        if (teacherId == null) {
            throw new IllegalArgumentException("TeacherId cannot be null");
        }

        BookingRequest bookingRequest = bookingRepository.findById(bookingId);

        if (bookingRequest == null) {
            throw new IllegalArgumentException("Booking request not found");
        }

        if (bookingRequest.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only pending booking requests can be rejected");
        }

        AvailabilitySlot slot = availabilityRepository.findById(bookingRequest.getSlotId());

        if (slot == null) {
            throw new IllegalArgumentException("Availability slot does not exist");
        }

        if (!slot.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException("Only the targeted teacher can respond to this booking request");
        }

        bookingRepository.updateStatus(bookingId, BookingStatus.REJECTED);

        return new BookingRequest(
                bookingRequest.getId(),
                bookingRequest.getSlotId(),
                bookingRequest.getRequesterId(),
                bookingRequest.getStartTime(),
                bookingRequest.getEndTime(),
                bookingRequest.getTitle(),
                bookingRequest.getDescription(),
                BookingStatus.REJECTED
        );
    }
}