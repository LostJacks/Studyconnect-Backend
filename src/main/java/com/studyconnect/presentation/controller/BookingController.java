package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.BookingService;
import com.studyconnect.domain.model.BookingRequest;
import com.studyconnect.presentation.dto.CreateBookingRequest;
import com.studyconnect.presentation.dto.UpdateBookingStatusRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingRequest createBooking(@Valid @RequestBody CreateBookingRequest request) {
        return bookingService.createBooking(request);
    }

    @PatchMapping("/{bookingId}")
    public BookingRequest updateBookingStatus(
            @PathVariable Long bookingId,
            @Valid @RequestBody UpdateBookingStatusRequest request) {

        return bookingService.updateBookingStatus(
                bookingId,
                request.getTeacherId(),
                request.getStatus()
        );
    }
}