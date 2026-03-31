package com.studyconnect.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class AvailabilitySlot {

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private boolean booked;
}