package com.example.dentistapp.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ScheduleRequest {


    private Long clientId;


    private Long dentistId;


    private LocalDateTime appointmentDate;


    private String status;

}