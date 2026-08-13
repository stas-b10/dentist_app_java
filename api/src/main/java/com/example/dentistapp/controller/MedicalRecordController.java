package com.example.dentistapp.controller;

import com.example.dentistapp.dto.MedicalRecordRequest;
import com.example.dentistapp.dto.MedicalRecordResponse;
import com.example.dentistapp.service.MedicalRecordService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;


    @PostMapping
    public MedicalRecordResponse create(
            @RequestBody MedicalRecordRequest request
    ) {

        return medicalRecordService.create(request);
    }


    @GetMapping("/{id}")
    public MedicalRecordResponse getById(
            @PathVariable Long id
    ) {

        return medicalRecordService.getById(id);
    }


    @GetMapping("/client/{clientId}")
    public List<MedicalRecordResponse> getClientRecords(
            @PathVariable Long clientId
    ) {

        return medicalRecordService
                .getClientRecords(clientId);
    }


    @GetMapping("/dentist/{dentistId}")
    public List<MedicalRecordResponse> getDentistRecords(
            @PathVariable Long dentistId
    ) {

        return medicalRecordService
                .getDentistRecords(dentistId);
    }
}