package com.example.dentistapp.service;

import com.example.dentistapp.dto.DentistDashboardResponse;
import com.example.dentistapp.model.Dentist;
import com.example.dentistapp.model.User;
import com.example.dentistapp.repository.AppointmentRepository;
import com.example.dentistapp.repository.DentistRepository;
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

    public DentistDashboardResponse getDashboard() {

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

        Dentist dentist = dentistRepository
                .findByUser(user)
                .orElseThrow(() ->
                        new RuntimeException("Dentist profile not found")
                );

        long todaysAppointments =
                appointmentRepository
                        .findByDentistIdAndAppointmentDateOrderByStartTime(
                                dentist.getId(),
                                LocalDate.now()
                        )
                        .stream()
                        .filter(a ->
                                !a.getStatus().equals("CANCELLED")
                        )
                        .count();

        long pendingRequests =
                appointmentRepository.countByDentistIdAndStatus(
                        dentist.getId(),
                        "PENDING"
                );

        long completedAppointments =
                appointmentRepository.countByDentistIdAndStatus(
                        dentist.getId(),
                        "COMPLETED"
                );

        return DentistDashboardResponse.builder()
                .todaysAppointments(todaysAppointments)
                .pendingRequests(pendingRequests)
                .patients(0)
                .messages(0)
                .completedAppointments(completedAppointments)
                .dentistName(
                        dentist.getFirstName()
                                + " "
                                + dentist.getLastName()
                )
                .build();
    }
}