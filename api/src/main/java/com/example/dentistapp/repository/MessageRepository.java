package com.example.dentistapp.repository;

import com.example.dentistapp.model.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository
        extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderBySentAtAsc(
            Long conversationId
    );


    @Query("""
            SELECT COUNT(m)
            FROM Message m
            WHERE m.conversation.client.id = :userId
               OR m.conversation.dentist.id = :userId
            """)
    long countMessagesForUser(
            @Param("userId") Long userId
    );
}