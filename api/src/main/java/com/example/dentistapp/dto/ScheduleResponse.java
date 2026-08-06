package com.example.dentistapp.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@Builder
public class ScheduleResponse {


    private Long id;


    private Long clientId;


    private Long dentistId;


    private LocalDateTime appointmentDate;


    private String status;

}