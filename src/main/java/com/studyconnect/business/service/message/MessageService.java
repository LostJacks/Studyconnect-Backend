package com.studyconnect.business.service.message;

import com.studyconnect.data.entity.message.MessageEntity;
import com.studyconnect.data.entity.message.SessionChatEntity;
import com.studyconnect.data.repository.message.MessageRepository;
import com.studyconnect.data.repository.message.SessionChatRepository;
import com.studyconnect.presentation.dto.message.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {

    private final SessionChatRepository sessionChatRepository;
    private final MessageRepository messageRepository;

    @Override
    public List<MessageResponse> getMessages(Long bookingId) {

        SessionChatEntity chat =
                sessionChatRepository
                        .findByBookingId(bookingId)
                        .orElseThrow();

        return messageRepository
                .findByChatIdOrderBySentAtAsc(chat.getId())
                .stream()
                .map(message ->
                        new MessageResponse(
                                message.getId(),
                                message.getSenderId(),
                                message.getContent(),
                                message.getSentAt()
                        )
                )
                .toList();
    }

    @Override
    public MessageResponse sendMessage(
            Long bookingId,
            Long senderId,
            String content
    ) {

        SessionChatEntity chat =
                sessionChatRepository
                        .findByBookingId(bookingId)
                        .orElseGet(() ->
                                sessionChatRepository.save(
                                        new SessionChatEntity(bookingId)
                                )
                        );

        MessageEntity savedMessage =
                messageRepository.save(
                        new MessageEntity(
                                chat.getId(),
                                senderId,
                                content
                        )
                );

        return new MessageResponse(
                savedMessage.getId(),
                savedMessage.getSenderId(),
                savedMessage.getContent(),
                savedMessage.getSentAt()
        );
    }
}