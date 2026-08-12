package com.example.dentistapp.controller;

import com.example.dentistapp.dto.DentistDashboardResponse;
import com.example.dentistapp.service.DentistDashboardService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dentist/dashboard")
@RequiredArgsConstructor
public class DentistDashboardController {

    private final DentistDashboardService dashboardService;

    @GetMapping
    public DentistDashboardResponse getDashboard() {

        return dashboardService.getDashboard();
    }
}