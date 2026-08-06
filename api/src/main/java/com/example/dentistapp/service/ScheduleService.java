package com.example.dentistapp.service;


import com.example.dentistapp.dto.ScheduleRequest;
import com.example.dentistapp.dto.ScheduleResponse;
import com.example.dentistapp.model.Client;
import com.example.dentistapp.model.Dentist;
import com.example.dentistapp.model.Schedule;
import com.example.dentistapp.repository.ClientRepository;
import com.example.dentistapp.repository.DentistRepository;
import com.example.dentistapp.repository.ScheduleRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ScheduleService {


    private final ScheduleRepository scheduleRepository;

    private final ClientRepository clientRepository;

    private final DentistRepository dentistRepository;



    public ScheduleResponse create(
            ScheduleRequest request
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


        Schedule schedule = Schedule.builder()
                .client(client)
                .dentist(dentist)
                .appointmentDate(
                        request.getAppointmentDate()
                )
                .status(
                        request.getStatus()
                )
                .build();



        scheduleRepository.save(schedule);


        return map(schedule);

    }




    public List<ScheduleResponse> getAll(){

        return scheduleRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());

    }



    private ScheduleResponse map(
            Schedule schedule
    ){

        return ScheduleResponse.builder()
                .id(schedule.getId())
                .clientId(
                        schedule.getClient().getId()
                )
                .dentistId(
                        schedule.getDentist().getId()
                )
                .appointmentDate(
                        schedule.getAppointmentDate()
                )
                .status(
                        schedule.getStatus()
                )
                .build();

    }

}