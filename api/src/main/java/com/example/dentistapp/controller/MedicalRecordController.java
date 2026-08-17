package com.example.dentistapp.controller;

import com.example.dentistapp.dto.MedicalRecordRequest;
import com.example.dentistapp.dto.MedicalRecordResponse;
import com.example.dentistapp.model.Client;
import com.example.dentistapp.repository.ClientRepository;
import com.example.dentistapp.service.MedicalRecordService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;
    private final ClientRepository clientRepository;


    // =========================================================
    // CREATE MEDICAL RECORD
    // DENTIST ONLY
    // =========================================================

    @PostMapping
    public MedicalRecordResponse create(
            @RequestBody MedicalRecordRequest request
    ) {

        System.out.println();
        System.out.println("=========================================");
        System.out.println("MEDICAL RECORD CREATE");
        System.out.println("=========================================");

        System.out.println("Request = " + request);

        MedicalRecordResponse response =
                medicalRecordService.create(request);

        System.out.println("CREATE SUCCESS");
        System.out.println("Response = " + response);

        System.out.println("=========================================");
        System.out.println();

        return response;
    }


    // =========================================================
    // GET ONE MEDICAL RECORD
    // =========================================================

    @GetMapping("/{id}")
    public MedicalRecordResponse getById(
            @PathVariable Long id
    ) {

        System.out.println();
        System.out.println("=========================================");
        System.out.println("MEDICAL RECORD GET BY ID");
        System.out.println("=========================================");

        System.out.println("ID = " + id);

        MedicalRecordResponse response =
                medicalRecordService.getById(id);

        System.out.println("GET BY ID SUCCESS");
        System.out.println("Response = " + response);

        System.out.println("=========================================");
        System.out.println();

        return response;
    }


    // =========================================================
    // GET CLIENT MEDICAL RECORDS
    //
    // CLIENT:
    // only their own records
    //
    // DENTIST:
    // can also access records through this endpoint
    // if needed
    // =========================================================

    @GetMapping("/client/{clientId}")
    public List<MedicalRecordResponse> getClientRecords(
            @PathVariable Long clientId,
            Authentication authentication
    ) {

        System.out.println();
        System.out.println("=========================================");
        System.out.println("GET CLIENT MEDICAL RECORDS");
        System.out.println("=========================================");

        System.out.println("Requested client ID = " + clientId);


        // -----------------------------------------------------
        // AUTHENTICATION CHECK
        // -----------------------------------------------------

        if (authentication == null) {

            System.out.println("AUTHENTICATION = NULL");

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication required"
            );
        }


        System.out.println(
                "Authenticated user = "
                        + authentication.getName()
        );

        System.out.println(
                "Authorities = "
                        + authentication.getAuthorities()
        );


        // -----------------------------------------------------
        // FIND CLIENT USING LOGGED-IN USER
        // -----------------------------------------------------

        String email = authentication.getName();

        Client client =
                clientRepository
                        .findByUserEmail(email)
                        .orElseThrow(
                                () -> new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Client not found"
                                )
                        );


        System.out.println(
                "Authenticated client ID = "
                        + client.getId()
        );


        // -----------------------------------------------------
        // CLIENT CAN ONLY SEE THEIR OWN RECORDS
        // -----------------------------------------------------

        boolean isDentist =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority
                                                .getAuthority()
                                                .equals("ROLE_DENTIST")
                        );


        if (!isDentist &&
                !client.getId().equals(clientId)) {

            System.out.println(
                    "ACCESS DENIED - client tried to access another client"
            );

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only access your own medical records"
            );
        }


        // -----------------------------------------------------
        // LOAD RECORDS
        // -----------------------------------------------------

        List<MedicalRecordResponse> records =
                medicalRecordService.getClientRecords(clientId);


        System.out.println(
                "Medical records found = "
                        + records.size()
        );

        System.out.println("=========================================");
        System.out.println();

        return records;
    }


    // =========================================================
    // GET DENTIST MEDICAL RECORDS
    // DENTIST ONLY
    // =========================================================

    @GetMapping("/dentist/{dentistId}")
    public List<MedicalRecordResponse> getDentistRecords(
            @PathVariable Long dentistId
    ) {

        System.out.println();
        System.out.println("=========================================");
        System.out.println("GET DENTIST MEDICAL RECORDS");
        System.out.println("=========================================");

        System.out.println(
                "Dentist ID = "
                        + dentistId
        );

        List<MedicalRecordResponse> records =
                medicalRecordService
                        .getDentistRecords(dentistId);

        System.out.println(
                "Records count = "
                        + records.size()
        );

        System.out.println("=========================================");
        System.out.println();

        return records;
    }
}