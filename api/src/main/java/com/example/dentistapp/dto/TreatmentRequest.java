package com.example.dentistapp.dto;

import lombok.Data;

@Data
public class TreatmentRequest {

    private Long appointmentId;

    private String title;

    private String description;
}