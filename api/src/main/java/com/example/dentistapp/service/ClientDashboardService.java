package com.example.dentistapp.service;

import com.example.dentistapp.dto.ClientDashboardResponse;
import com.example.dentistapp.model.Client;
import com.example.dentistapp.model.User;

import com.example.dentistapp.repository.AppointmentRepository;
import com.example.dentistapp.repository.ClientRepository;
import com.example.dentistapp.repository.MessageRepository;
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

    private final MessageRepository messageRepository;


    public ClientDashboardResponse getDashboard() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }


        String email =
                authentication.getName();


        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        Client client =
                clientRepository
                        .findByUser(user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Client profile not found"
                                )
                        );


        var appointments =
                appointmentRepository
                        .findByClientIdOrderByAppointmentDateDescStartTimeDesc(
                                client.getId()
                        );


        long upcoming =
                appointments
                        .stream()
                        .filter(a ->
                                !a.getAppointmentDate()
                                        .isBefore(
                                                LocalDate.now()
                                        )
                        )
                        .filter(a ->
                                !"CANCELLED".equals(
                                        a.getStatus()
                                )
                        )
                        .filter(a ->
                                !"REJECTED".equals(
                                        a.getStatus()
                                )
                        )
                        .count();


        long previous =
                appointments
                        .stream()
                        .filter(a ->
                                a.getAppointmentDate()
                                        .isBefore(
                                                LocalDate.now()
                                        )
                        )
                        .count();


        long messages =
                messageRepository
                        .countMessagesForUser(
                                user.getId()
                        );


        String firstName =
                client.getFirstName() == null
                        ? ""
                        : client.getFirstName();


        String lastName =
                client.getLastName() == null
                        ? ""
                        : client.getLastName();


        String clientName =
                (firstName + " " + lastName)
                        .trim();


        return ClientDashboardResponse.builder()
                .upcomingAppointments(upcoming)
                .previousAppointments(previous)
                .messages(messages)
                .clientName(clientName)
                .build();
    }
}