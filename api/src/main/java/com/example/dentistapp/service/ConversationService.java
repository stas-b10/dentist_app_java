package com.example.dentistapp.service;

import com.example.dentistapp.dto.ConversationResponse;
import com.example.dentistapp.model.Conversation;
import com.example.dentistapp.model.Role;
import com.example.dentistapp.model.User;
import com.example.dentistapp.repository.ConversationRepository;
import com.example.dentistapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;


    // =========================================================
    // GET MY CONVERSATIONS
    // =========================================================

    @Transactional(readOnly = true)
    public List<ConversationResponse> getForCurrentUser() {

        User me = getAuthenticatedUser();

        List<Conversation> conversations;

        if (me.getRole() == Role.CLIENT) {

            conversations =
                    conversationRepository
                            .findByClientIdOrderByUpdatedAtDesc(
                                    me.getId()
                            );

        } else if (me.getRole() == Role.DENTIST) {

            conversations =
                    conversationRepository
                            .findByDentistIdOrderByUpdatedAtDesc(
                                    me.getId()
                            );

        } else {

            throw new RuntimeException(
                    "Only clients and dentists can use conversations"
            );
        }


        return conversations
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    // =========================================================
    // CREATE OR GET ONE CONVERSATION
    // =========================================================

    @Transactional
    public ConversationResponse createOrGet(
            Long otherUserId
    ) {

        if (otherUserId == null) {

            throw new RuntimeException(
                    "Other user ID is required"
            );
        }


        User me = getAuthenticatedUser();


        User other =
                userRepository
                        .findById(otherUserId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "User not found"
                                )
                        );


        // =====================================================
        // DETERMINE CLIENT / DENTIST
        // =====================================================

        User client;

        User dentist;


        if (me.getRole() == Role.CLIENT) {

            client = me;
            dentist = other;

        } else if (me.getRole() == Role.DENTIST) {

            dentist = me;
            client = other;

        } else {

            throw new RuntimeException(
                    "Only clients and dentists can create conversations"
            );
        }


        // =====================================================
        // VERIFY ROLES
        // =====================================================

        if (client.getRole() != Role.CLIENT) {

            throw new RuntimeException(
                    "The other participant must be a client"
            );
        }


        if (dentist.getRole() != Role.DENTIST) {

            throw new RuntimeException(
                    "The other participant must be a dentist"
            );
        }


        // =====================================================
        // FIND EXISTING CONVERSATION
        // =====================================================

        var existing =
                conversationRepository
                        .findByClientIdAndDentistId(
                                client.getId(),
                                dentist.getId()
                        );


        if (existing.isPresent()) {

            return map(
                    existing.get()
            );
        }


        // =====================================================
        // CREATE NEW CONVERSATION
        // =====================================================

        LocalDateTime now =
                LocalDateTime.now();


        Conversation conversation =
                Conversation.builder()
                        .client(client)
                        .dentist(dentist)
                        .createdAt(now)
                        .updatedAt(now)
                        .build();


        try {

            conversation =
                    conversationRepository.saveAndFlush(
                            conversation
                    );

        } catch (Exception exception) {

            /*
             * This protects against two requests arriving
             * at exactly the same time.
             *
             * The database unique constraint prevents
             * the second conversation from being inserted.
             *
             * If that happens, retrieve the already-created
             * conversation.
             */

            existing =
                    conversationRepository
                            .findByClientIdAndDentistId(
                                    client.getId(),
                                    dentist.getId()
                            );


            if (existing.isPresent()) {

                return map(
                        existing.get()
                );
            }


            throw exception;
        }


        return map(
                conversation
        );
    }


    // =========================================================
    // CURRENT USER
    // =========================================================

    private User getAuthenticatedUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (
                authentication == null
                || !authentication.isAuthenticated()
        ) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }


        String email =
                authentication.getName();


        return userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );
    }


    // =========================================================
    // MAP
    // =========================================================

    private ConversationResponse map(
            Conversation conversation
    ) {

        return ConversationResponse
                .builder()
                .id(
                        conversation.getId()
                )
                .clientId(
                        conversation
                                .getClient()
                                .getId()
                )
                .dentistId(
                        conversation
                                .getDentist()
                                .getId()
                )
                .build();
    }
}