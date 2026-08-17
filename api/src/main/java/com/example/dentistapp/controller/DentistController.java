package com.example.dentistapp.controller;

import com.example.dentistapp.dto.AppointmentResponse;
import com.example.dentistapp.dto.ClientResponse;
import com.example.dentistapp.dto.DentistRequest;
import com.example.dentistapp.dto.DentistResponse;
import com.example.dentistapp.dto.ScheduleResponse;
import com.example.dentistapp.dto.TreatmentResponse;
import com.example.dentistapp.service.DentistService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dentists")
@RequiredArgsConstructor
public class DentistController {

    private final DentistService dentistService;


    // =========================
    // CREATE
    // =========================

    @PostMapping
    public DentistResponse create(
            @RequestBody DentistRequest request
    ) {
        return dentistService.create(request);
    }


    // =========================
    // GET ALL
    // =========================

    @GetMapping
    public List<DentistResponse> getAll() {
        return dentistService.getAll();
    }


    // =========================
    // CURRENT DENTIST
    // =========================

    @GetMapping("/me")
    public DentistResponse getCurrentDentist() {
        return dentistService.getCurrentDentist();
    }


    // =========================
    // GET BY ID
    // =========================

    @GetMapping("/{id}")
    public DentistResponse getById(
            @PathVariable Long id
    ) {
        return dentistService.getById(id);
    }


    // =========================
    // APPOINTMENTS
    // =========================

    @GetMapping("/{id}/appointments")
    public List<AppointmentResponse> getAppointments(
            @PathVariable Long id
    ) {
        return dentistService.getAppointments(id);
    }


    // =========================
    // PATIENTS
    // =========================

    @GetMapping("/{id}/patients")
    public List<ClientResponse> getPatients(
            @PathVariable Long id
    ) {
        return dentistService.getPatients(id);
    }


    // =========================
    // TREATMENTS
    // =========================

    @GetMapping("/{id}/treatments")
    public List<TreatmentResponse> getTreatments(
            @PathVariable Long id
    ) {
        return dentistService.getTreatments(id);
    }


    // =========================
    // SEARCH
    // =========================

    @GetMapping("/search/name")
    public List<DentistResponse> searchByName(
            @RequestParam String name
    ) {
        return dentistService.searchByName(name);
    }


    @GetMapping("/search/specialization")
    public List<DentistResponse> searchBySpecialization(
            @RequestParam String specialization
    ) {
        return dentistService.searchBySpecialization(
                specialization
        );
    }


    @GetMapping("/search/clinic")
    public List<DentistResponse> searchByClinic(
            @RequestParam String clinic
    ) {
        return dentistService.searchByClinic(clinic);
    }


    // =========================
    // UPDATE
    // =========================

    @PutMapping("/{id}")
    public DentistResponse update(
            @PathVariable Long id,
            @RequestBody DentistRequest request
    ) {
        return dentistService.update(id, request);
    }


    // =========================
    // DELETE
    // =========================

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {
        dentistService.delete(id);
    }
}