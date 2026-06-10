package com.studyconnect.business.service.message;

import com.studyconnect.data.entity.message.MessageEntity;
import com.studyconnect.data.entity.message.SessionChatEntity;
import com.studyconnect.data.repository.message.MessageRepository;
import com.studyconnect.data.repository.message.SessionChatRepository;
import com.studyconnect.presentation.dto.message.MessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageServiceTest {

    private SessionChatRepository sessionChatRepository;
    private MessageRepository messageRepository;
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        sessionChatRepository = mock(SessionChatRepository.class);
        messageRepository = mock(MessageRepository.class);

        messageService = new MessageService(
                sessionChatRepository,
                messageRepository
        );
    }

    @Test
    void getMessages_existingChat_returnsMessages() {
        SessionChatEntity chat = mock(SessionChatEntity.class);
        MessageEntity message = mock(MessageEntity.class);
        LocalDateTime sentAt = LocalDateTime.of(2026, 5, 30, 10, 0);

        when(chat.getId()).thenReturn(5L);
        when(sessionChatRepository.findByBookingId(1L))
                .thenReturn(Optional.of(chat));

        when(message.getId()).thenReturn(10L);
        when(message.getSenderId()).thenReturn(2L);
        when(message.getContent()).thenReturn("Hello");
        when(message.getSentAt()).thenReturn(sentAt);

        when(messageRepository.findByChatIdOrderBySentAtAsc(5L))
                .thenReturn(List.of(message));

        List<MessageResponse> result = messageService.getMessages(1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).getId());
        assertEquals(2L, result.get(0).getSenderId());
        assertEquals("Hello", result.get(0).getContent());
        assertEquals(sentAt, result.get(0).getSentAt());

        verify(sessionChatRepository).findByBookingId(1L);
        verify(sessionChatRepository, never()).save(any(SessionChatEntity.class));
        verify(messageRepository).findByChatIdOrderBySentAtAsc(5L);
    }

    @Test
    void getMessages_noChat_createsChatAndReturnsMessages() {
        SessionChatEntity savedChat = mock(SessionChatEntity.class);

        when(savedChat.getId()).thenReturn(5L);

        when(sessionChatRepository.findByBookingId(1L))
                .thenReturn(Optional.empty());

        when(sessionChatRepository.save(any(SessionChatEntity.class)))
                .thenReturn(savedChat);

        when(messageRepository.findByChatIdOrderBySentAtAsc(5L))
                .thenReturn(List.of());

        List<MessageResponse> result = messageService.getMessages(1L);

        assertTrue(result.isEmpty());

        verify(sessionChatRepository).findByBookingId(1L);
        verify(sessionChatRepository).save(any(SessionChatEntity.class));
        verify(messageRepository).findByChatIdOrderBySentAtAsc(5L);
    }

    @Test
    void sendMessage_existingChat_savesMessageAndReturnsResponse() {
        SessionChatEntity chat = mock(SessionChatEntity.class);
        MessageEntity savedMessage = mock(MessageEntity.class);
        LocalDateTime sentAt = LocalDateTime.of(2026, 5, 30, 10, 0);

        when(chat.getId()).thenReturn(5L);

        when(sessionChatRepository.findByBookingId(1L))
                .thenReturn(Optional.of(chat));

        when(savedMessage.getId()).thenReturn(10L);
        when(savedMessage.getSenderId()).thenReturn(2L);
        when(savedMessage.getContent()).thenReturn("Hello");
        when(savedMessage.getSentAt()).thenReturn(sentAt);

        when(messageRepository.save(any(MessageEntity.class)))
                .thenReturn(savedMessage);

        MessageResponse result = messageService.sendMessage(1L, 2L, "Hello");

        assertEquals(10L, result.getId());
        assertEquals(2L, result.getSenderId());
        assertEquals("Hello", result.getContent());
        assertEquals(sentAt, result.getSentAt());

        verify(sessionChatRepository).findByBookingId(1L);
        verify(sessionChatRepository, never()).save(any(SessionChatEntity.class));
        verify(messageRepository).save(any(MessageEntity.class));
    }

    @Test
    void sendMessage_noChat_createsChatSavesMessageAndReturnsResponse() {
        SessionChatEntity savedChat = mock(SessionChatEntity.class);
        MessageEntity savedMessage = mock(MessageEntity.class);
        LocalDateTime sentAt = LocalDateTime.of(2026, 5, 30, 10, 0);

        when(savedChat.getId()).thenReturn(5L);

        when(sessionChatRepository.findByBookingId(1L))
                .thenReturn(Optional.empty());

        when(sessionChatRepository.save(any(SessionChatEntity.class)))
                .thenReturn(savedChat);

        when(savedMessage.getId()).thenReturn(10L);
        when(savedMessage.getSenderId()).thenReturn(2L);
        when(savedMessage.getContent()).thenReturn("Hello");
        when(savedMessage.getSentAt()).thenReturn(sentAt);

        when(messageRepository.save(any(MessageEntity.class)))
                .thenReturn(savedMessage);

        MessageResponse result = messageService.sendMessage(1L, 2L, "Hello");

        assertEquals(10L, result.getId());
        assertEquals(2L, result.getSenderId());
        assertEquals("Hello", result.getContent());
        assertEquals(sentAt, result.getSentAt());

        verify(sessionChatRepository).findByBookingId(1L);
        verify(sessionChatRepository).save(any(SessionChatEntity.class));
        verify(messageRepository).save(any(MessageEntity.class));
    }
}