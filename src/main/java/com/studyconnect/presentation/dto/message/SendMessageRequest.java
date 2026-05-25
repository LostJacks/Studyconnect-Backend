package com.studyconnect.presentation.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {

    @NotBlank(message = "Message content is required")
    @Size(max = 1000, message = "Message cannot be longer than 1000 characters")
    private String content;
}