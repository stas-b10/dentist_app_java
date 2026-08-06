package com.example.dentistapp.dto;

import lombok.Data;

import java.time.LocalTime;

@Data
public class ScheduleRequest {

    private Long dentistId;

    private String dayOfWeek;

    private LocalTime startTime;

    private LocalTime endTime;
}