package com.studyconnect.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class CreateAvailabilityRequest {

    private Long teacherId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

}