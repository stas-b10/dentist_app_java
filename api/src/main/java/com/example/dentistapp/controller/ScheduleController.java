package com.example.dentistapp.controller;

import com.example.dentistapp.dto.ScheduleRequest;
import com.example.dentistapp.dto.ScheduleResponse;
import com.example.dentistapp.service.ScheduleService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;


    @PostMapping("/schedules")
    public ScheduleResponse create(
            @RequestBody ScheduleRequest request
    ) {
        return scheduleService.create(request);
    }


    @GetMapping("/schedules")
    public List<ScheduleResponse> getAll() {
        return scheduleService.getAll();
    }


    @GetMapping("/dentists/{dentistId}/schedule")
    public List<ScheduleResponse> getByDentist(
            @PathVariable Long dentistId
    ) {
        return scheduleService.getByDentist(dentistId);
    }


    @GetMapping("/dentists/{dentistId}/schedule/{dayOfWeek}")
    public List<ScheduleResponse> getByDentistAndDay(
            @PathVariable Long dentistId,
            @PathVariable String dayOfWeek
    ) {
        return scheduleService.getByDentistAndDay(
                dentistId,
                dayOfWeek
        );
    }
}