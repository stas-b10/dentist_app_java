package com.example.dentistapp.service;

import com.example.dentistapp.dto.DentistAvailabilityRequest;
import com.example.dentistapp.dto.DentistAvailabilityResponse;

import com.example.dentistapp.model.Dentist;
import com.example.dentistapp.model.DentistAvailability;

import com.example.dentistapp.repository.DentistAvailabilityRepository;
import com.example.dentistapp.repository.DentistRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DentistAvailabilityService {

    private final DentistAvailabilityRepository availabilityRepository;

    private final DentistRepository dentistRepository;


    public DentistAvailabilityResponse create(
            Long dentistId,
            DentistAvailabilityRequest request
    ) {

        Dentist dentist =
                dentistRepository
                        .findById(dentistId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dentist not found"
                                )
                        );


        if (request.getDayOfWeek() == null) {
            throw new RuntimeException(
                    "Day of week is required"
            );
        }


        if (request.getStartTime() == null) {
            throw new RuntimeException(
                    "Start time is required"
            );
        }


        if (request.getEndTime() == null) {
            throw new RuntimeException(
                    "End time is required"
            );
        }


        if (request.getStartTime()
                .isAfter(request.getEndTime())
                ||
                request.getStartTime()
                        .equals(request.getEndTime())) {

            throw new RuntimeException(
                    "Start time must be before end time"
            );
        }


        if (availabilityRepository
                .findByDentistAndDayOfWeek(
                        dentist,
                        request.getDayOfWeek()
                )
                .isPresent()) {

            throw new RuntimeException(
                    "Availability already exists for this day"
            );
        }


        DentistAvailability availability =
                DentistAvailability.builder()
                        .dentist(dentist)
                        .dayOfWeek(
                                request.getDayOfWeek()
                        )
                        .startTime(
                                request.getStartTime()
                        )
                        .endTime(
                                request.getEndTime()
                        )
                        .build();


        availabilityRepository.save(
                availability
        );


        return map(availability);
    }


    public List<DentistAvailabilityResponse> getByDentist(
            Long dentistId
    ) {

        Dentist dentist =
                dentistRepository
                        .findById(dentistId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dentist not found"
                                )
                        );


        return availabilityRepository
                .findByDentist(dentist)
                .stream()
                .map(this::map)
                .toList();
    }


    private DentistAvailabilityResponse map(
            DentistAvailability availability
    ) {

        return DentistAvailabilityResponse.builder()
                .id(availability.getId())
                .dayOfWeek(
                        availability.getDayOfWeek()
                )
                .startTime(
                        availability.getStartTime()
                )
                .endTime(
                        availability.getEndTime()
                )
                .build();
    }
}