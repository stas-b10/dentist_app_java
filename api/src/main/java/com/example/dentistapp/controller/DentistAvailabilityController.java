package com.example.dentistapp.controller;

import com.example.dentistapp.dto.DentistAvailabilityRequest;
import com.example.dentistapp.dto.DentistAvailabilityResponse;
import com.example.dentistapp.service.DentistAvailabilityService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dentists/{dentistId}/availability")
@RequiredArgsConstructor
public class DentistAvailabilityController {

    private final DentistAvailabilityService availabilityService;


    @PostMapping
    public DentistAvailabilityResponse create(
            @PathVariable Long dentistId,
            @RequestBody DentistAvailabilityRequest request
    ) {

        return availabilityService.create(
                dentistId,
                request
        );
    }


    @GetMapping
    public List<DentistAvailabilityResponse> getByDentist(
            @PathVariable Long dentistId
    ) {

        return availabilityService.getByDentist(
                dentistId
        );
    }
}