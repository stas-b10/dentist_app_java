package com.example.dentistapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDashboardResponse {

    private long upcomingAppointments;

    private long previousAppointments;

    private long messages;

    private String clientName;
}