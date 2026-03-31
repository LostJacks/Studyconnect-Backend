package com.studyconnect.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class BookingRequestEntity {

    private Long id;
    private Long slotId;
    private Long requesterId;
    private LocalTime startTime;
    private LocalTime endTime;
    private String title;
    private String description;
    private String status;
}