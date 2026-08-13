package com.example.dentistapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponse {

    private Long id;

    private Long conversationId;

    private Long senderId;

    private String content;

    private LocalDateTime sentAt;
}