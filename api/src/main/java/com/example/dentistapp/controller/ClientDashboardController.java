package com.example.dentistapp.controller;

import com.example.dentistapp.dto.ClientDashboardResponse;
import com.example.dentistapp.service.ClientDashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/client/dashboard")
@RequiredArgsConstructor
public class ClientDashboardController {

    private final ClientDashboardService dashboardService;

    @GetMapping
    public ClientDashboardResponse getDashboard() {

        return dashboardService.getDashboard();
    }
}