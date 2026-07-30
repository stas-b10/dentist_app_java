package com.example.dentistapp.service;

import com.example.dentistapp.model.*;
import com.example.dentistapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;



@Service
@RequiredArgsConstructor
public class ChatService {


    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;



    public Message sendMessage(
            Conversation conversation,
            User sender,
            String text
    ){
        Message message = Message.builder()
                .conversation(conversation)
                .sender(sender)
                .content(text)
                .sentAt(LocalDateTime.now())
                .build();
        return messageRepository.save(message);

    }
 
    public List<Message> getMessages(
            Long conversationId
    ){
        return messageRepository
                .findByConversationId(
                        conversationId
                );

    }


}