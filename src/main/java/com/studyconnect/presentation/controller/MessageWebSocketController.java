package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.message.IMessageService;
import com.studyconnect.presentation.dto.message.MessageResponse;
import com.studyconnect.presentation.dto.message.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final IMessageService messageService;

    @MessageMapping("/session/{bookingId}/send")
    @SendTo("/topic/session/{bookingId}")
    public MessageResponse sendMessage(
            @DestinationVariable Long bookingId,
            @Payload SendMessageRequest request
    ) {

        Long senderId = 1L;

        return messageService.sendMessage(
                bookingId,
                senderId,
                request.getContent()
        );
    }
}