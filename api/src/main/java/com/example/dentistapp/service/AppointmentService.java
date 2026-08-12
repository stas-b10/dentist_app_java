package com.example.dentistapp.service;


import com.example.dentistapp.dto.AppointmentRequest;
import com.example.dentistapp.dto.AppointmentResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.dentistapp.model.*;

import com.example.dentistapp.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class AppointmentService {


    private final AppointmentRepository appointmentRepository;


    private final ClientRepository clientRepository;


    private final DentistRepository dentistRepository;



    public AppointmentResponse create(
            AppointmentRequest request
    ){


        Client client =
                clientRepository.findById(
                        request.getClientId()
                )
                .orElseThrow(
                        () -> new RuntimeException("Client not found")
                );



        Dentist dentist =
                dentistRepository.findById(
                        request.getDentistId()
                )
                .orElseThrow(
                        () -> new RuntimeException("Dentist not found")
                );



        Appointment appointment =
                Appointment.builder()
                .client(client)
                .dentist(dentist)
                .appointmentDate(
                        request.getAppointmentDate()
                )
                .startTime(
                        request.getStartTime()
                )
                .endTime(
                        request.getEndTime()
                )
                .status("PENDING")
                .build();



        appointmentRepository.save(appointment);


        return map(appointment);

    }



    public List<AppointmentResponse> getAll(){


        return appointmentRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());

    }





    private AppointmentResponse map(
            Appointment appointment
    ){

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .clientId(
                        appointment.getClient().getId()
                )
                .dentistId(
                        appointment.getDentist().getId()
                )
                .appointmentDate(
                        appointment.getAppointmentDate()
                )
                .startTime(
                        appointment.getStartTime()
                )
                .endTime(
                        appointment.getEndTime()
                )
                .status(
                        appointment.getStatus()
                )
                .build();

    }

    public List<AppointmentResponse> getDentistPendingRequests() {

    Dentist dentist = getAuthenticatedDentist();

    return appointmentRepository
            .findByDentistIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
                    dentist.getId(),
                    "PENDING"
            )
            .stream()
            .map(this::map)
            .collect(Collectors.toList());
}


public AppointmentResponse accept(Long appointmentId) {

    Dentist dentist = getAuthenticatedDentist();

    Appointment appointment =
            appointmentRepository
                    .findByIdAndDentistId(
                            appointmentId,
                            dentist.getId()
                    )
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "Appointment not found"
                            )
                    );

    if (!"PENDING".equals(appointment.getStatus())) {
        throw new RuntimeException(
                "Appointment is not pending"
        );
    }

    appointment.setStatus("ACCEPTED");

    appointmentRepository.save(appointment);

    return map(appointment);
}


public AppointmentResponse reject(Long appointmentId) {

    Dentist dentist = getAuthenticatedDentist();

    Appointment appointment =
            appointmentRepository
                    .findByIdAndDentistId(
                            appointmentId,
                            dentist.getId()
                    )
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "Appointment not found"
                            )
                    );

    if (!"PENDING".equals(appointment.getStatus())) {
        throw new RuntimeException(
                "Appointment is not pending"
        );
    }

    appointment.setStatus("REJECTED");

    appointmentRepository.save(appointment);

    return map(appointment);
}


private Dentist getAuthenticatedDentist() {

    Authentication authentication =
            SecurityContextHolder
                    .getContext()
                    .getAuthentication();

    String email = authentication.getName();

    return dentistRepository
            .findByUserEmail(email)
            .orElseThrow(
                    () -> new RuntimeException(
                            "Dentist profile not found"
                    )
            );
}

}