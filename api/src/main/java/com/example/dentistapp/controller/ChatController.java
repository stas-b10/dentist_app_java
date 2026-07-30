package com.example.dentistapp.controller;


import com.example.dentistapp.model.Message;„
import com.example.dentistapp.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;



@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/{conversationId}")
    public List<Message> getMessages(
            @PathVariable Long conversationId
    ){
        return chatService
                .getMessages(
                    conversationId
                );
    }


}