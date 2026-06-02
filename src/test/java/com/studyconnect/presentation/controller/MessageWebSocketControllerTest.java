package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.JwtService;
import com.studyconnect.business.service.message.IMessageService;
import com.studyconnect.data.entity.UserEntity;
import com.studyconnect.data.irepository.IUserRepository;
import com.studyconnect.presentation.dto.message.MessageResponse;
import com.studyconnect.presentation.dto.message.SendMessageRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class MessageWebSocketControllerTest {

    @Test
    void sendMessage_validAuthorizationHeader_sendsMessage() {
        IMessageService messageService = mock(IMessageService.class);
        IUserRepository userRepository = mock(IUserRepository.class);
        JwtService jwtService = mock(JwtService.class);

        MessageWebSocketController controller =
                new MessageWebSocketController(
                        messageService,
                        userRepository,
                        jwtService
                );

        SendMessageRequest request = new SendMessageRequest();
        request.setContent("Hello");

        UserEntity sender = new UserEntity(
                1L,
                "Student User",
                "student@test.com",
                "password",
                "STUDENT"
        );

        MessageResponse response = new MessageResponse(
                10L,
                1L,
                "Hello",
                LocalDateTime.of(2026, 5, 30, 10, 0)
        );

        when(jwtService.extractEmail("valid-token"))
                .thenReturn("student@test.com");

        when(userRepository.findEntityByEmail("student@test.com"))
                .thenReturn(sender);

        when(messageService.sendMessage(5L, 1L, "Hello"))
                .thenReturn(response);

        MessageResponse result = controller.sendMessage(
                5L,
                request,
                "Bearer valid-token"
        );

        assertSame(response, result);

        verify(jwtService).extractEmail("valid-token");
        verify(userRepository).findEntityByEmail("student@test.com");
        verify(messageService).sendMessage(5L, 1L, "Hello");
    }
}