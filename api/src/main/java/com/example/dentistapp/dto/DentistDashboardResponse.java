package com.example.dentistapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DentistDashboardResponse {

    private long todaysAppointments;

    private long pendingRequests;

    private long patients;

    private long messages;

    private long completedAppointments;

    private String dentistName;
}