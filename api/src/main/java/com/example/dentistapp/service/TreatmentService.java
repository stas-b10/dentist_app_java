package com.example.dentistapp.service;


import com.example.dentistapp.dto.TreatmentRequest;
import com.example.dentistapp.dto.TreatmentResponse;

import com.example.dentistapp.model.Client;
import com.example.dentistapp.model.Dentist;
import com.example.dentistapp.model.Treatment;

import com.example.dentistapp.repository.ClientRepository;
import com.example.dentistapp.repository.DentistRepository;
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

    private final ClientRepository clientRepository;

    private final DentistRepository dentistRepository;



    public TreatmentResponse create(
            TreatmentRequest request
    ){


        Client client =
                clientRepository.findById(
                        request.getClientId()
                )
                .orElseThrow(
                        () -> new RuntimeException(
                                "Client not found"
                        )
                );



        Dentist dentist =
                dentistRepository.findById(
                        request.getDentistId()
                )
                .orElseThrow(
                        () -> new RuntimeException(
                                "Dentist not found"
                        )
                );



        Treatment treatment =
                Treatment.builder()
                        .client(client)
                        .dentist(dentist)
                        .title(request.getTitle())
                        .description(request.getDescription())
                        .createdAt(LocalDateTime.now())
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





    public List<TreatmentResponse> getByClient(
            Long clientId
    ){

        return treatmentRepository
                .findByClientId(clientId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());

    }




    public List<TreatmentResponse> getByDentist(
            Long dentistId
    ){

        return treatmentRepository
                .findByDentistId(dentistId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());

    }





    private TreatmentResponse map(
            Treatment treatment
    ){

        return TreatmentResponse.builder()
                .id(treatment.getId())
                .clientId(
                        treatment.getClient().getId()
                )
                .dentistId(
                        treatment.getDentist().getId()
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