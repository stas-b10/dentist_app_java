package com.example.dentistapp.dto;

import lombok.Data;

@Data
public class ReviewRequest {

    private Long appointmentId;

    private Integer rating;

    private String comment;
}