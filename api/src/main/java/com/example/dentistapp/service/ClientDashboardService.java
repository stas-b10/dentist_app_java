package com.example.dentistapp.service;

import com.example.dentistapp.dto.ClientDashboardResponse;
import com.example.dentistapp.model.Client;
import com.example.dentistapp.model.User;
import com.example.dentistapp.repository.AppointmentRepository;
import com.example.dentistapp.repository.ClientRepository;
import com.example.dentistapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ClientDashboardService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final AppointmentRepository appointmentRepository;

    public ClientDashboardResponse getDashboard() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        Client client = clientRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Client profile not found")
                );

        long upcoming = appointmentRepository
                .findByClientIdOrderByAppointmentDateDescStartTimeDesc(
                        client.getId()
                )
                .stream()
                .filter(a ->
                        !a.getAppointmentDate()
                                .isBefore(LocalDate.now())
                )
                .filter(a ->
                        !a.getStatus().equals("CANCELLED")
                )
                .count();

        long previous = appointmentRepository
                .findByClientIdOrderByAppointmentDateDescStartTimeDesc(
                        client.getId()
                )
                .stream()
                .filter(a ->
                        a.getAppointmentDate()
                                .isBefore(LocalDate.now())
                )
                .count();

        return ClientDashboardResponse.builder()
                .upcomingAppointments(upcoming)
                .previousAppointments(previous)
                .messages(0)
                .clientName(
                        client.getFirstName()
                                + " "
                                + client.getLastName()
                )
                .build();
    }
}