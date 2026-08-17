package com.example.dentistapp.service;

import com.example.dentistapp.dto.DentistDashboardResponse;
import com.example.dentistapp.model.Dentist;
import com.example.dentistapp.model.User;

import com.example.dentistapp.repository.AppointmentRepository;
import com.example.dentistapp.repository.DentistRepository;
import com.example.dentistapp.repository.MessageRepository;
import com.example.dentistapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DentistDashboardService {

    private final UserRepository userRepository;

    private final DentistRepository dentistRepository;

    private final AppointmentRepository appointmentRepository;

    private final MessageRepository messageRepository;


    public DentistDashboardResponse getDashboard() {

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


        Dentist dentist =
                dentistRepository
                        .findByUser(user)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dentist profile not found"
                                )
                        );


        long todaysAppointments =
                appointmentRepository
                        .findByDentistIdAndAppointmentDateOrderByStartTime(
                                dentist.getId(),
                                LocalDate.now()
                        )
                        .stream()
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


        long pendingRequests =
                appointmentRepository
                        .countByDentistIdAndStatus(
                                dentist.getId(),
                                "PENDING"
                        );


        long completedAppointments =
                appointmentRepository
                        .countByDentistIdAndStatus(
                                dentist.getId(),
                                "COMPLETED"
                        );


        long patients =
                appointmentRepository
                        .findByDentistId(
                                dentist.getId()
                        )
                        .stream()
                        .map(a ->
                                a.getClient().getId()
                        )
                        .distinct()
                        .count();


        long messages =
                messageRepository
                        .countMessagesForUser(
                                user.getId()
                        );


        String firstName =
                dentist.getFirstName() == null
                        ? ""
                        : dentist.getFirstName();


        String lastName =
                dentist.getLastName() == null
                        ? ""
                        : dentist.getLastName();


        String dentistName =
                (firstName + " " + lastName)
                        .trim();


        return DentistDashboardResponse.builder()
                .todaysAppointments(todaysAppointments)
                .pendingRequests(pendingRequests)
                .patients(patients)
                .messages(messages)
                .completedAppointments(
                        completedAppointments
                )
                .dentistName(dentistName)
                .build();
    }
}