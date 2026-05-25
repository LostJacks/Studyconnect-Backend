package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.message.IMessageService;
import com.studyconnect.presentation.dto.message.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {

    private final IMessageService messageService;

    @GetMapping("/{bookingId}")
    public List<MessageResponse> getMessages(
            @PathVariable Long bookingId
    ) {
        return messageService.getMessages(bookingId);
    }
}