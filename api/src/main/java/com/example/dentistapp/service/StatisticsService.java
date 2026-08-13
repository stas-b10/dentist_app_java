package com.example.dentistapp.service;

import com.example.dentistapp.dto.StatisticsResponse;
import com.example.dentistapp.model.Dentist;
import com.example.dentistapp.repository.AppointmentRepository;
import com.example.dentistapp.repository.DentistRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final AppointmentRepository appointmentRepository;

    private final DentistRepository dentistRepository;


    public StatisticsResponse getDentistStatistics() {

        Dentist dentist = getAuthenticatedDentist();

        Long dentistId = dentist.getId();


        long totalAppointments =
                appointmentRepository.countByDentistId(
                        dentistId
                );


        long pendingAppointments =
                appointmentRepository.countByDentistIdAndStatus(
                        dentistId,
                        "PENDING"
                );


        long acceptedAppointments =
                appointmentRepository.countByDentistIdAndStatus(
                        dentistId,
                        "ACCEPTED"
                );


        long rejectedAppointments =
                appointmentRepository.countByDentistIdAndStatus(
                        dentistId,
                        "REJECTED"
                );


        long completedAppointments =
                appointmentRepository.countByDentistIdAndStatus(
                        dentistId,
                        "COMPLETED"
                );


        long totalPatients =
                appointmentRepository
                        .findByDentistId(dentistId)
                        .stream()
                        .map(appointment ->
                                appointment.getClient().getId()
                        )
                        .distinct()
                        .count();


        return StatisticsResponse.builder()
                .totalAppointments(totalAppointments)
                .pendingAppointments(pendingAppointments)
                .acceptedAppointments(acceptedAppointments)
                .rejectedAppointments(rejectedAppointments)
                .completedAppointments(completedAppointments)
                .totalPatients(totalPatients)
                .build();
    }


    private Dentist getAuthenticatedDentist() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        String email =
                authentication.getName();


        return dentistRepository
                .findByUserEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Dentist profile not found"
                        )
                );
    }
}