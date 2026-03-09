package com.studyconnect.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class AvailabilitySlot {

    private Long helperId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

}