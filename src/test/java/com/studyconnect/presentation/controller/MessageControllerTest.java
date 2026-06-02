package com.studyconnect.presentation.controller;

import com.studyconnect.business.service.message.IMessageService;
import com.studyconnect.presentation.dto.message.MessageResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class MessageControllerTest {

    private final IMessageService messageService =
            mock(IMessageService.class);

    private final MessageController messageController =
            new MessageController(messageService);

    @Test
    void getMessages_returnsMessages() {
        List<MessageResponse> messages = List.of(
                new MessageResponse(
                        1L,
                        2L,
                        "Hello",
                        LocalDateTime.of(2026, 5, 30, 10, 0)
                )
        );

        when(messageService.getMessages(5L))
                .thenReturn(messages);

        List<MessageResponse> result =
                messageController.getMessages(5L);

        assertSame(messages, result);

        verify(messageService).getMessages(5L);
    }
}