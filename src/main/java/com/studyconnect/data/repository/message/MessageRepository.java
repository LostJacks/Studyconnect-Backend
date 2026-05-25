package com.studyconnect.data.repository.message;

import com.studyconnect.data.entity.message.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository
        extends JpaRepository<MessageEntity, Long> {

    List<MessageEntity> findByChatIdOrderBySentAtAsc(Long chatId);
}