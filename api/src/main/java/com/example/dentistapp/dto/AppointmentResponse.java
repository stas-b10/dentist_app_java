package com.example.dentistapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class AppointmentResponse {

    private Long id;

    private Long clientId;

    private Long dentistId;

    private LocalDate appointmentDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private String status;
}