package com.studyconnect.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookingStatusRequest {
    private Long teacherId;
    private String status;
}