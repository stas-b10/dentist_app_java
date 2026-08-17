package com.example.dentistapp.controller;

import com.example.dentistapp.dto.AppointmentResponse;
import com.example.dentistapp.dto.ClientRequest;
import com.example.dentistapp.dto.ClientResponse;
import com.example.dentistapp.service.ClientService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ClientResponse create(
            @RequestBody ClientRequest request
    ) {
        return clientService.create(request);
    }


    @GetMapping
    public List<ClientResponse> getAll() {
        return clientService.getAll();
    }

    @GetMapping("/{id}")
    public ClientResponse getById(
            @PathVariable Long id
    ) {
        return clientService.getById(id);
    }

    @GetMapping("/{id}/appointments")
    public List<AppointmentResponse> getAppointments(
            @PathVariable Long id
    ) {
        return clientService.getAppointments(id);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id
    ) {
        clientService.delete(id);
    }
}