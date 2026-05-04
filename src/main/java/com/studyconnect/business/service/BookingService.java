package com.studyconnect.business.service;

import com.studyconnect.data.irepository.IAvailabilityRepository;
import com.studyconnect.data.irepository.IBookingRepository;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.domain.model.AvailabilitySlot;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.domain.model.BookingStatus;
import com.studyconnect.domain.model.Role;
import com.studyconnect.domain.model.User;
import com.studyconnect.presentation.dto.BookingSessionResponse;
import com.studyconnect.presentation.dto.CreateBookingRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService implements IBookingService {

    private final IBookingRepository bookingRepository;
    private final IAvailabilityRepository availabilityRepository;
    private final IUserRepository userRepository;

    public BookingService(IBookingRepository bookingRepository,
                          IAvailabilityRepository availabilityRepository,
                          IUserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingRequest createBooking(CreateBookingRequest request) {
        User requester = userRepository.findById(request.getRequesterId());

        if (requester == null) {
            throw new IllegalArgumentException("Student does not exist");
        }

        if (requester.getRole() != Role.STUDENT) {
            throw new IllegalArgumentException("Only students can create booking requests");
        }

        AvailabilitySlot slot = availabilityRepository.findById(request.getSlotId());

        if (slot == null) {
            throw new IllegalArgumentException("Availability slot does not exist");
        }

        if (slot.isBooked()) {
            throw new IllegalArgumentException("This slot is already booked");
        }

        BookingRequest bookingRequest = new BookingRequest(
                slot.getStartTime(),
                slot.getEndTime(),
                request.getTitle(),
                request.getDescription(),
                BookingStatus.PENDING
        );

        return bookingRepository.saveForSlotAndRequester(
                bookingRequest,
                request.getSlotId(),
                request.getRequesterId()
        );
    }

    @Override
    public List<BookingSessionResponse> getBookingsByUser(Long userId) {
        User user = userRepository.findById(userId);

        if (user == null) {
            throw new IllegalArgumentException("User does not exist");
        }

        return bookingRepository.findBookingsByUserId(userId);
    }

    @Override
    public BookingRequest updateBookingStatus(Long bookingId, Long teacherId, BookingStatus status) {
        BookingRequest bookingRequest = bookingRepository.findById(bookingId);

        if (bookingRequest == null) {
            throw new IllegalArgumentException("Booking request not found");
        }

        if (bookingRequest.getStatus() != BookingStatus.PENDING) {
            throw new IllegalArgumentException("Only pending booking requests can be updated");
        }

        Long slotId = bookingRepository.findSlotIdByBookingId(bookingId);

        if (slotId == null) {
            throw new IllegalArgumentException("Availability slot does not exist");
        }

        AvailabilitySlot slot = availabilityRepository.findById(slotId);

        if (slot == null) {
            throw new IllegalArgumentException("Availability slot does not exist");
        }

        Long slotTeacherId = availabilityRepository.findTeacherIdBySlotId(slotId);

        if (slotTeacherId == null) {
            throw new IllegalArgumentException("Availability slot does not exist");
        }

        if (!slotTeacherId.equals(teacherId)) {
            throw new IllegalArgumentException("Only the targeted teacher can respond to this booking request");
        }

        if (status != BookingStatus.ACCEPTED && status != BookingStatus.REJECTED) {
            throw new IllegalArgumentException("Status must be ACCEPTED or REJECTED");
        }

        if (status == BookingStatus.ACCEPTED) {
            if (slot.isBooked()) {
                throw new IllegalArgumentException("This slot is already booked");
            }

            availabilityRepository.updateBookedStatus(slotId, true);
        }

        if (status == BookingStatus.REJECTED) {
            availabilityRepository.updateBookedStatus(slotId, false);
        }

        bookingRepository.updateStatus(bookingId, status);

        return new BookingRequest(
                bookingRequest.getStartTime(),
                bookingRequest.getEndTime(),
                bookingRequest.getTitle(),
                bookingRequest.getDescription(),
                status
        );
    }
}