package com.example.dentistapp.controller;

import com.example.dentistapp.dto.ConversationRequest;
import com.example.dentistapp.dto.ConversationResponse;
import com.example.dentistapp.service.ConversationService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping
    public List<ConversationResponse> getMyConversations() {
        return conversationService.getForCurrentUser();
    }

    @PostMapping
    public ConversationResponse createOrGet(
            @RequestBody ConversationRequest request
    ) {
        return conversationService.createOrGet(
                request.getOtherUserId()
        );
    }
}