package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.JwtService;
import com.studyconnect.business.service.message.IMessageService;
import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.presentation.dto.message.MessageResponse;
import com.studyconnect.presentation.dto.message.SendMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final IMessageService messageService;
    private final IUserRepository userRepository;
    private final JwtService jwtService;

    @MessageMapping("/session/{bookingId}/send")
    @SendTo("/topic/session/{bookingId}")
    public MessageResponse sendMessage(
            @DestinationVariable Long bookingId,
            @Payload SendMessageRequest request,
            @Header("Authorization") String authorizationHeader
    ) {
        String token = authorizationHeader.substring(7);
        String email = jwtService.extractEmail(token);

        UserEntity sender = userRepository.findEntityByEmail(email);

        return messageService.sendMessage(
                bookingId,
                sender.getId(),
                request.getContent()
        );
    }
}