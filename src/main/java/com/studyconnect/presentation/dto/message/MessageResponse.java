package com.studyconnect.presentation.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageResponse {

    private Long id;

    private Long senderId;

    private String content;

    private LocalDateTime sentAt;
}