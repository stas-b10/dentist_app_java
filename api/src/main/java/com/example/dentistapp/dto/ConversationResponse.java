package com.example.dentistapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConversationResponse {

    private Long id;

    private Long clientId;   // User id of the client participant

    private Long dentistId;  // User id of the dentist participant
}