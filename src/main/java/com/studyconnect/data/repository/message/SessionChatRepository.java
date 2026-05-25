package com.studyconnect.data.repository.message;

import com.studyconnect.data.entity.message.SessionChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SessionChatRepository
        extends JpaRepository<SessionChatEntity, Long> {

    Optional<SessionChatEntity> findByBookingId(Long bookingId);
}