package com.example.dentistapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TreatmentResponse {

    private Long id;

    private Long appointmentId;

    private String title;

    private String description;

    private LocalDateTime createdAt;
}