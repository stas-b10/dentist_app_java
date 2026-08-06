package com.example.dentistapp.service;


import com.example.dentistapp.dto.TreatmentRequest;
import com.example.dentistapp.dto.TreatmentResponse;

import com.example.dentistapp.model.Appointment;
import com.example.dentistapp.model.Treatment;

import com.example.dentistapp.repository.AppointmentRepository;
import com.example.dentistapp.repository.TreatmentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class TreatmentService {


    private final TreatmentRepository treatmentRepository;

    private final AppointmentRepository appointmentRepository;



    public TreatmentResponse create(
            TreatmentRequest request
    ){


        Appointment appointment =
                appointmentRepository.findById(
                        request.getAppointmentId()
                )
                .orElseThrow(
                        () -> new RuntimeException(
                                "Appointment not found"
                        )
                );



        Treatment treatment =
                Treatment.builder()
                        .appointment(appointment)
                        .title(
                                request.getTitle()
                        )
                        .description(
                                request.getDescription()
                        )
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();



        treatmentRepository.save(treatment);



        return map(treatment);

    }





    public List<TreatmentResponse> getAll(){


        return treatmentRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());

    }





    public TreatmentResponse getById(
            Long id
    ){

        Treatment treatment =
                treatmentRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Treatment not found"
                        )
                );


        return map(treatment);

    }





    public List<TreatmentResponse> getByAppointment(
            Long appointmentId
    ){

        return treatmentRepository
                .findByAppointmentId(appointmentId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());

    }





    public void delete(
            Long id
    ){

        treatmentRepository.deleteById(id);

    }





    private TreatmentResponse map(
            Treatment treatment
    ){

        return TreatmentResponse.builder()
                .id(
                        treatment.getId()
                )
                .appointmentId(
                        treatment.getAppointment().getId()
                )
                .title(
                        treatment.getTitle()
                )
                .description(
                        treatment.getDescription()
                )
                .createdAt(
                        treatment.getCreatedAt()
                )
                .build();

    }

}