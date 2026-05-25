package com.studyconnect.business.service.message;

import com.studyconnect.presentation.dto.message.MessageResponse;

import java.util.List;

public interface IMessageService {

    List<MessageResponse> getMessages(Long bookingId);

    MessageResponse sendMessage(
            Long bookingId,
            Long senderId,
            String content
    );
}