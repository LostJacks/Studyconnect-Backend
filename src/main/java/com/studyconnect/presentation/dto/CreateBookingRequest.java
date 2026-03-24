package com.studyconnect.presentation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class CreateBookingRequest {

    private Long slotId;
    private Long requesterId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String title;
    private String description;

}