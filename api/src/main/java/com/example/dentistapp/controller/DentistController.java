package com.example.dentistapp.controller;

import com.example.dentistapp.dto.DentistRequest;
import com.example.dentistapp.dto.DentistResponse;
import com.example.dentistapp.service.DentistService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dentists")
@RequiredArgsConstructor
public class DentistController {

    private final DentistService dentistService;

    @PostMapping
    public DentistResponse create(
            @RequestBody DentistRequest request
    ) {

        return dentistService.create(request);

    }

    @GetMapping
    public List<DentistResponse> getAll() {

        return dentistService.getAll();

    }

    @GetMapping("/{id}")
    public DentistResponse getById(
            @PathVariable Long id
    ) {

        return dentistService.getById(id);

    }

    @PutMapping("/{id}")
    public DentistResponse update(
            @PathVariable Long id,
            @RequestBody DentistRequest request
    ) {

        return dentistService.update(id, request);

    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {

        dentistService.delete(id);

    }

}