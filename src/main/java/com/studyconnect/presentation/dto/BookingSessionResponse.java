package com.studyconnect.presentation.dto;

import com.studyconnect.domain.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class BookingSessionResponse {

    private Long id;

    private Long studentId;
    private String studentName;

    private Long teacherId;
    private String teacherName;

    private Long slotId;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    private String title;
    private String description;

    private BookingStatus status;
}