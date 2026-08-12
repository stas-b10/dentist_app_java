package com.example.dentistapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TreatmentResponse {

    private Long id;

    private String name;

    private String description;

    private Integer durationMinutes;

    private Double price;
}