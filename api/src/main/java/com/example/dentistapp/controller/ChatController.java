package com.example.dentistapp.controller;

import com.example.dentistapp.dto.ChatMessageRequest;
import com.example.dentistapp.dto.ChatMessageResponse;
import com.example.dentistapp.service.ChatService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public ChatMessageResponse sendMessage(
            ChatMessageRequest request
    ) {

        return chatService.sendMessage(
                request.getConversationId(),
                request.getContent()
        );
    }

    @GetMapping("/conversation/{conversationId}")
    public List<ChatMessageResponse> getMessages(
            @PathVariable Long conversationId
    ) {

        return chatService.getMessages(
                conversationId
        );
    }
}