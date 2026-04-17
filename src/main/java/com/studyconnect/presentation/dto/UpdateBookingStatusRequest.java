package com.studyconnect.presentation.dto;

import com.studyconnect.domain.model.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBookingStatusRequest {

    @NotNull(message = "TeacherId is required")
    private Long teacherId;

    @NotNull(message = "Status is required")
    private BookingStatus status;
}