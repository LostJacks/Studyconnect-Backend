package com.studyconnect.domain.model;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class AvailabilitySlot {

    private final LocalDate date;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private boolean booked;
    private boolean available;

    public AvailabilitySlot(
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime,
            boolean booked,
            boolean available
    ) {
        if (date == null) {
            throw new IllegalArgumentException("Date is required");
        }

        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Start time and end time are required");
        }

        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.booked = booked;
        this.available = available;
    }

    public void markAsBooked() {
        if (booked) {
            throw new IllegalStateException("Availability slot is already booked");
        }

        this.booked = true;
        this.available = false;
    }

    public void markAsAvailable() {
        if (booked) {
            throw new IllegalStateException("Booked slots cannot be made available");
        }

        this.available = true;
    }

    public void markAsUnavailable() {
        if (booked) {
            throw new IllegalStateException("Booked slots cannot be made unavailable");
        }

        this.available = false;
    }

    public void toggleAvailability() {
        if (booked) {
            throw new IllegalStateException("Booked slots cannot be modified");
        }

        this.available = !this.available;
    }

    public boolean isAvailable() {
        return available && !booked;
    }
}