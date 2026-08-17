package com.example.dentistapp.service;

import com.example.dentistapp.dto.ScheduleRequest;
import com.example.dentistapp.dto.ScheduleResponse;
import com.example.dentistapp.model.Dentist;
import com.example.dentistapp.model.Schedule;
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

    private final DentistRepository dentistRepository;


    // =========================
    // CREATE
    // =========================

    public ScheduleResponse create(
            ScheduleRequest request
    ) {

        Dentist dentist =
                dentistRepository
                        .findById(request.getDentistId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dentist not found"
                                )
                        );

        Schedule schedule =
                Schedule.builder()
                        .dentist(dentist)
                        .dayOfWeek(request.getDayOfWeek())
                        .startTime(request.getStartTime())
                        .endTime(request.getEndTime())
                        .build();

        scheduleRepository.save(schedule);

        return map(schedule);
    }


    // =========================
    // GET ALL
    // =========================

    public List<ScheduleResponse> getAll() {

        return scheduleRepository
                .findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    // =========================
    // GET BY DENTIST
    // =========================

    public List<ScheduleResponse> getByDentist(
            Long dentistId
    ) {

        return scheduleRepository
                .findByDentistId(dentistId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    // =========================
    // GET BY DENTIST + DAY
    // =========================

    public List<ScheduleResponse> getByDentistAndDay(
            Long dentistId,
            String dayOfWeek
    ) {

        return scheduleRepository
                .findByDentistIdAndDayOfWeek(
                        dentistId,
                        dayOfWeek
                )
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    // =========================
    // MAPPER
    // =========================

    private ScheduleResponse map(
            Schedule schedule
    ) {

        return ScheduleResponse.builder()
                .id(schedule.getId())
                .dentistId(
                        schedule.getDentist().getId()
                )
                .dayOfWeek(
                        schedule.getDayOfWeek()
                )
                .startTime(
                        schedule.getStartTime()
                )
                .endTime(
                        schedule.getEndTime()
                )
                .build();
    }
}