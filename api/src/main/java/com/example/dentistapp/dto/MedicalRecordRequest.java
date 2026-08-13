package com.example.dentistapp.dto;

import lombok.Data;

@Data
public class MedicalRecordRequest {

    private Long clientId;

    private Long dentistId;

    private Long appointmentId;

    private String diagnosis;

    private String notes;

    private String treatmentPerformed;
}