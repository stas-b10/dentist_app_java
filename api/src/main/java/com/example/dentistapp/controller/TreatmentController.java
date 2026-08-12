package com.example.dentistapp.controller;

import com.example.dentistapp.dto.TreatmentRequest;
import com.example.dentistapp.dto.TreatmentResponse;
import com.example.dentistapp.service.TreatmentService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/treatments")
@RequiredArgsConstructor
public class TreatmentController {

    private final TreatmentService treatmentService;

    @PostMapping
    public TreatmentResponse create(
            @RequestBody TreatmentRequest request
    ) {

        return treatmentService.create(request);
    }

    @GetMapping
    public List<TreatmentResponse> getAll() {

        return treatmentService.getAll();
    }

    @GetMapping("/{id}")
    public TreatmentResponse getById(
            @PathVariable Long id
    ) {

        return treatmentService.getById(id);
    }

    @PutMapping("/{id}")
    public TreatmentResponse update(
            @PathVariable Long id,
            @RequestBody TreatmentRequest request
    ) {

        return treatmentService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {

        treatmentService.delete(id);
    }
}