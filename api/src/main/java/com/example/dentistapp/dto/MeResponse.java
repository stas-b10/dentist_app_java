package com.example.dentistapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MeResponse {

    private Long userId;

    private String email;

    private String role;

    private ClientResponse client;   // present when role == CLIENT

    private DentistResponse dentist; // present when role == DENTIST
}