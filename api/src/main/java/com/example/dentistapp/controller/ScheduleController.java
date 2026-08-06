package com.example.dentistapp.controller;


import com.example.dentistapp.dto.ScheduleRequest;
import com.example.dentistapp.dto.ScheduleResponse;
import com.example.dentistapp.service.ScheduleService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {


    private final ScheduleService scheduleService;



    @PostMapping
    public ScheduleResponse create(
            @RequestBody ScheduleRequest request
    ){

        return scheduleService.create(request);

    }



    @GetMapping
    public List<ScheduleResponse> getAll(){

        return scheduleService.getAll();

    }

}