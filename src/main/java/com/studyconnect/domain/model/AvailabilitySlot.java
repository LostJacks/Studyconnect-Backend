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

    public AvailabilitySlot(LocalDate date, LocalTime startTime, LocalTime endTime, boolean booked) {
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
    }

    public void markAsBooked() {
        if (booked) {
            throw new IllegalStateException("Availability slot is already booked");
        }

        this.booked = true;
    }

    public void markAsAvailable() {
        if (!booked) {
            throw new IllegalStateException("Availability slot is already available");
        }

        this.booked = false;
    }

    public boolean isAvailable() {
        return !booked;
    }
}