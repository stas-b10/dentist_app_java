package com.example.dentistapp.dto;

import lombok.Data;

@Data
public class ChatMessageRequest {

    private Long conversationId;

    private String content;
}