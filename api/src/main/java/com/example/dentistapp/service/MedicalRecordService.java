package com.example.dentistapp.service;

import com.example.dentistapp.dto.MedicalRecordRequest;
import com.example.dentistapp.dto.MedicalRecordResponse;
import com.example.dentistapp.model.Appointment;
import com.example.dentistapp.model.Client;
import com.example.dentistapp.model.Dentist;
import com.example.dentistapp.model.MedicalRecord;

import com.example.dentistapp.repository.AppointmentRepository;
import com.example.dentistapp.repository.ClientRepository;
import com.example.dentistapp.repository.DentistRepository;
import com.example.dentistapp.repository.MedicalRecordRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    private final ClientRepository clientRepository;

    private final DentistRepository dentistRepository;

    private final AppointmentRepository appointmentRepository;


    // =========================================================
    // CREATE
    // =========================================================

    public MedicalRecordResponse create(
            MedicalRecordRequest request
    ) {

        Client client =
                clientRepository
                        .findById(request.getClientId())
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Client not found"
                                )
                        );


        Dentist dentist =
                dentistRepository
                        .findById(request.getDentistId())
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Dentist not found"
                                )
                        );


        Appointment appointment = null;


        if (request.getAppointmentId() != null) {

            appointment =
                    appointmentRepository
                            .findById(
                                    request.getAppointmentId()
                            )
                            .orElseThrow(
                                    () -> new RuntimeException(
                                            "Appointment not found"
                                    )
                            );
        }


        MedicalRecord record =
                MedicalRecord.builder()
                        .client(client)
                        .dentist(dentist)
                        .appointment(appointment)
                        .diagnosis(request.getDiagnosis())
                        .notes(request.getNotes())
                        .treatmentPerformed(
                                request.getTreatmentPerformed()
                        )
                        .createdAt(LocalDateTime.now())
                        .build();


        MedicalRecord saved =
                medicalRecordRepository.save(record);


        return map(saved);
    }


    // =========================================================
    // CLIENT RECORDS
    // =========================================================

    public List<MedicalRecordResponse> getClientRecords(
            Long clientId
    ) {

        return medicalRecordRepository
                .findByClientIdOrderByCreatedAtDesc(clientId)
                .stream()
                .map(this::map)
                .toList();
    }


    // =========================================================
    // DENTIST RECORDS
    // =========================================================

    public List<MedicalRecordResponse> getDentistRecords(
            Long dentistId
    ) {

        return medicalRecordRepository
                .findByDentistIdOrderByCreatedAtDesc(dentistId)
                .stream()
                .map(this::map)
                .toList();
    }


    // =========================================================
    // GET ONE
    // =========================================================

    public MedicalRecordResponse getById(
            Long id
    ) {

        MedicalRecord record =
                medicalRecordRepository
                        .findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Medical record not found"
                                )
                        );


        return map(record);
    }


    // =========================================================
    // MAP
    // =========================================================

    private MedicalRecordResponse map(
            MedicalRecord record
    ) {

        return MedicalRecordResponse.builder()

                .id(record.getId())

                .clientId(
                        record.getClient().getId()
                )

                .dentistId(
                        record.getDentist().getId()
                )

                .appointmentId(
                        record.getAppointment() != null
                                ? record.getAppointment().getId()
                                : null
                )

                .diagnosis(
                        record.getDiagnosis()
                )

                .notes(
                        record.getNotes()
                )

                .treatmentPerformed(
                        record.getTreatmentPerformed()
                )

                .createdAt(
                        record.getCreatedAt()
                )

                .build();
    }
}