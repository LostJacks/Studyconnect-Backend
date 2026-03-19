package com.studyconnect.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class BookingRequest {

    private Long id;
    private Long slotId;
    private Long requesterId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String title;
    private String description;

}