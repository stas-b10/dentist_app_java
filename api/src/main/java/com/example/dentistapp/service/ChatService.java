package com.example.dentistapp.service;

import com.example.dentistapp.dto.ChatMessageResponse;
import com.example.dentistapp.model.Conversation;
import com.example.dentistapp.model.Message;
import com.example.dentistapp.model.User;
import com.example.dentistapp.repository.ConversationRepository;
import com.example.dentistapp.repository.MessageRepository;
import com.example.dentistapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;

    private final ConversationRepository conversationRepository;

    private final UserRepository userRepository;


    public ChatMessageResponse sendMessage(
            Long conversationId,
            String content
    ) {

        User sender = getAuthenticatedUser();

        Conversation conversation =
                getConversation(conversationId);

        verifyParticipant(
                conversation,
                sender
        );

        if (content == null || content.isBlank()) {
            throw new RuntimeException(
                    "Message cannot be empty"
            );
        }

        Message message =
                Message.builder()
                        .conversation(conversation)
                        .sender(sender)
                        .content(content)
                        .sentAt(LocalDateTime.now())
                        .build();

        messageRepository.save(message);

        return map(message);
    }


    public List<ChatMessageResponse> getMessages(
            Long conversationId
    ) {

        User user = getAuthenticatedUser();

        Conversation conversation =
                getConversation(conversationId);

        verifyParticipant(
                conversation,
                user
        );

        return messageRepository
                .findByConversationIdOrderBySentAtAsc(
                        conversationId
                )
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );
    }


    private Conversation getConversation(
            Long conversationId
    ) {

        return conversationRepository
                .findById(conversationId)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Conversation not found"
                        )
                );
    }


    private void verifyParticipant(
            Conversation conversation,
            User user
    ) {

        boolean isClient =
                conversation.getClient() != null
                &&
                conversation.getClient()
                        .getId()
                        .equals(user.getId());

        boolean isDentist =
                conversation.getDentist() != null
                &&
                conversation.getDentist()
                        .getId()
                        .equals(user.getId());

        if (!isClient && !isDentist) {

            throw new RuntimeException(
                    "You are not a participant of this conversation"
            );
        }
    }


    private ChatMessageResponse map(
            Message message
    ) {

        return ChatMessageResponse.builder()
                .id(message.getId())
                .conversationId(
                        message.getConversation().getId()
                )
                .senderId(
                        message.getSender().getId()
                )
                .content(
                        message.getContent()
                )
                .sentAt(
                        message.getSentAt()
                )
                .build();
    }
}