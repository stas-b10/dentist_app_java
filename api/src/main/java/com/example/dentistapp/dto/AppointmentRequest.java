package com.example.dentistapp.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {

    private Long clientId;

    private Long dentistId;

    private Long treatmentId;

    private LocalDate appointmentDate;

    private LocalTime startTime;
}