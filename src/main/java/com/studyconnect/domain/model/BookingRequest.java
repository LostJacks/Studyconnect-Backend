package com.studyconnect.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class BookingRequest {

    private LocalTime startTime;
    private LocalTime endTime;
    private String title;
    private String description;
    private BookingStatus status;
}