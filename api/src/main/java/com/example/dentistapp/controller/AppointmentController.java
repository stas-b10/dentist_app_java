package com.example.dentistapp.controller;


import com.example.dentistapp.dto.AppointmentRequest;
import com.example.dentistapp.dto.AppointmentResponse;
import com.example.dentistapp.service.AppointmentService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {


    private final AppointmentService appointmentService;



    @PostMapping
    public AppointmentResponse create(
            @RequestBody AppointmentRequest request
    ){

        return appointmentService.create(request);

    }



    @GetMapping
    public List<AppointmentResponse> getAll(){

        return appointmentService.getAll();

    }

}