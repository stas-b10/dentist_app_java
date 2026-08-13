package com.example.dentistapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {

    private Long id;

    private Long clientId;

    private Long dentistId;

    private Long appointmentId;

    private Integer rating;

    private String comment;

    private LocalDateTime createdAt;
}