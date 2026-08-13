package com.example.dentistapp.controller;

import com.example.dentistapp.dto.StatisticsResponse;
import com.example.dentistapp.service.StatisticsService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;


    @GetMapping("/dentist")
    public StatisticsResponse getDentistStatistics() {

        return statisticsService.getDentistStatistics();
    }
}