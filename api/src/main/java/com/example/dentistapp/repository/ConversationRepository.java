package com.example.dentistapp.repository;

import com.example.dentistapp.model.Conversation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository
        extends JpaRepository<Conversation, Long> {


    Optional<Conversation> findByIdAndClientId(
            Long conversationId,
            Long clientId
    );


    Optional<Conversation> findByIdAndDentistId(
            Long conversationId,
            Long dentistId
    );


    List<Conversation> findByClientIdOrderByUpdatedAtDesc(
            Long clientUserId
    );


    List<Conversation> findByDentistIdOrderByUpdatedAtDesc(
            Long dentistUserId
    );

    Optional<Conversation> findByClientIdAndDentistId(
            Long clientUserId,
            Long dentistUserId
    );
}