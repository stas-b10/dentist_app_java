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
    ) {

        return appointmentService.create(request);
    }

    @GetMapping
    public List<AppointmentResponse> getAll() {

        return appointmentService.getAll();
    }

    @GetMapping("/dentist/pending")
    public List<AppointmentResponse> getDentistPendingRequests() {

        return appointmentService.getDentistPendingRequests();
    }

    @PutMapping("/dentist/{appointmentId}/accept")
    public AppointmentResponse accept(
            @PathVariable Long appointmentId
    ) {

        return appointmentService.accept(appointmentId);
    }

    @PutMapping("/dentist/{appointmentId}/reject")
    public AppointmentResponse reject(
            @PathVariable Long appointmentId
    ) {

        return appointmentService.reject(appointmentId);
    }
}