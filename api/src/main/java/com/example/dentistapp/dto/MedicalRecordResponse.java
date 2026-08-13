package com.example.dentistapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class MedicalRecordResponse {

    private Long id;

    private Long clientId;

    private Long dentistId;

    private Long appointmentId;

    private String diagnosis;

    private String notes;

    private String treatmentPerformed;

    private LocalDateTime createdAt;
}