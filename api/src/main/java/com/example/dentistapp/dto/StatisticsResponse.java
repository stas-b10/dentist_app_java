package com.example.dentistapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StatisticsResponse {

    private long totalAppointments;

    private long pendingAppointments;

    private long acceptedAppointments;

    private long rejectedAppointments;

    private long completedAppointments;

    private long totalPatients;
}