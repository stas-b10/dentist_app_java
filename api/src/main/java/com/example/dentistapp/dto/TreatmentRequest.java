package com.example.dentistapp.dto;

import lombok.Data;

@Data
public class TreatmentRequest {

    private String name;

    private String description;

    private Integer durationMinutes;

    private Double price;
}