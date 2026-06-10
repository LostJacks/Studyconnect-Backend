package com.studyconnect.domain.model;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public class BookingRequest {

    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String title;
    private final String description;
    private BookingStatus status;

    public BookingRequest(
            LocalTime startTime,
            LocalTime endTime,
            String title,
            String description
    ) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time are required");
        }

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }

        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.description = description;
        this.status = BookingStatus.PENDING;
    }

    public BookingRequest(
            LocalTime startTime,
            LocalTime endTime,
            String title,
            String description,
            BookingStatus status
    ) {
        this(startTime, endTime, title, description);

        if (status == null) {
            throw new IllegalArgumentException("Booking status cannot be null");
        }

        this.status = status;
    }

    public void accept() {
        if (status != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be accepted");
        }

        this.status = BookingStatus.ACCEPTED;
    }

    public void reject() {
        if (status != BookingStatus.PENDING) {
            throw new IllegalStateException("Only pending bookings can be rejected");
        }

        this.status = BookingStatus.REJECTED;
    }

    public void cancel() {
        if (status == BookingStatus.COMPLETED) {
            throw new IllegalStateException("Completed bookings cannot be cancelled");
        }

        this.status = BookingStatus.CANCELLED;
    }

    public boolean isPending() {
        return status == BookingStatus.PENDING;
    }
}