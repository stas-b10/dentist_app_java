package com.example.dentistapp.service;

import com.example.dentistapp.dto.AppointmentResponse;
import com.example.dentistapp.dto.ClientResponse;
import com.example.dentistapp.dto.DentistRequest;
import com.example.dentistapp.dto.DentistResponse;
import com.example.dentistapp.dto.ScheduleResponse;
import com.example.dentistapp.dto.TreatmentResponse;

import com.example.dentistapp.model.Appointment;
import com.example.dentistapp.model.Dentist;
import com.example.dentistapp.model.Schedule;
import com.example.dentistapp.model.Treatment;
import com.example.dentistapp.model.User;

import com.example.dentistapp.repository.AppointmentRepository;
import com.example.dentistapp.repository.ClientRepository;
import com.example.dentistapp.repository.DentistRepository;
import com.example.dentistapp.repository.DentistTreatmentRepository;
import com.example.dentistapp.repository.ScheduleRepository;
import com.example.dentistapp.repository.UserRepository;

import org.springframework.security.core.context.SecurityContextHolder;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DentistService {

    private final DentistRepository dentistRepository;

    private final UserRepository userRepository;

    private final DentistTreatmentRepository dentistTreatmentRepository;

    private final AppointmentRepository appointmentRepository;

    private final ClientRepository clientRepository;

    private final ScheduleRepository scheduleRepository;


    // =========================
    // TREATMENTS
    // =========================

    public List<TreatmentResponse> getTreatments(
            Long dentistId
    ) {

        return dentistTreatmentRepository
                .findByDentistId(dentistId)
                .stream()
                .map(dt -> {

                    Treatment treatment = dt.getTreatment();

                    return TreatmentResponse.builder()
                            .id(treatment.getId())
                            .name(treatment.getName())
                            .description(
                                    treatment.getDescription()
                            )
                            .durationMinutes(
                                    treatment.getDurationMinutes()
                            )
                            .price(treatment.getPrice())
                            .build();
                })
                .collect(Collectors.toList());
    }


    // =========================
    // APPOINTMENTS
    // =========================

    public List<AppointmentResponse> getAppointments(
            Long dentistId
    ) {

        return appointmentRepository
                .findByDentistId(dentistId)
                .stream()
                .map(this::mapAppointment)
                .collect(Collectors.toList());
    }


    // =========================
    // PATIENTS
    // =========================

    public List<ClientResponse> getPatients(
            Long dentistId
    ) {

        return appointmentRepository
                .findByDentistId(dentistId)
                .stream()
                .map(Appointment::getClient)
                .distinct()
                .map(client ->
                        ClientResponse.builder()
                                .id(client.getId())
                                .userId(
                                        client.getUser().getId()
                                )
                                .firstName(
                                        client.getFirstName()
                                )
                                .lastName(
                                        client.getLastName()
                                )
                                .phone(
                                        client.getPhone()
                                )
                                .dateOfBirth(
                                        client.getDateOfBirth()
                                )
                                .profileImage(
                                        client.getProfileImage()
                                )
                                .build()
                )
                .collect(Collectors.toList());
    }


    // =========================
    // SCHEDULE
    // =========================

    public List<ScheduleResponse> getSchedule(
            Long dentistId
    ) {

        return scheduleRepository
                .findByDentistId(dentistId)
                .stream()
                .map(this::mapSchedule)
                .collect(Collectors.toList());
    }


    // =========================
    // CREATE
    // =========================

    public DentistResponse create(
            DentistRequest request
    ) {

        User user =
                userRepository
                        .findById(request.getUserId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        Dentist dentist =
                Dentist.builder()
                        .user(user)
                        .firstName(
                                request.getFirstName()
                        )
                        .lastName(
                                request.getLastName()
                        )
                        .specialization(
                                request.getSpecialization()
                        )
                        .phone(
                                request.getPhone()
                        )
                        .clinicName(
                                request.getClinicName()
                        )
                        .experienceYears(
                                request.getExperienceYears()
                        )
                        .biography(
                                request.getBiography()
                        )
                        .profileImage(
                                request.getProfileImage()
                        )
                        .build();

        dentistRepository.save(dentist);

        return map(dentist);
    }


    // =========================
    // GET ALL
    // =========================

    public List<DentistResponse> getAll() {

        return dentistRepository
                .findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    // =========================
    // GET BY ID
    // =========================

    public DentistResponse getById(
            Long id
    ) {

        Dentist dentist =
                dentistRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dentist not found"
                                )
                        );

        return map(dentist);
    }


    // =========================
    // UPDATE
    // =========================

    public DentistResponse update(
            Long id,
            DentistRequest request
    ) {

        Dentist dentist =
                dentistRepository
                        .findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dentist not found"
                                )
                        );

        dentist.setFirstName(
                request.getFirstName()
        );

        dentist.setLastName(
                request.getLastName()
        );

        dentist.setSpecialization(
                request.getSpecialization()
        );

        dentist.setPhone(
                request.getPhone()
        );

        dentist.setClinicName(
                request.getClinicName()
        );

        dentist.setExperienceYears(
                request.getExperienceYears()
        );

        dentist.setBiography(
                request.getBiography()
        );

        dentist.setProfileImage(
                request.getProfileImage()
        );

        dentistRepository.save(dentist);

        return map(dentist);
    }


    // =========================
    // DELETE
    // =========================

    public void delete(
            Long id
    ) {

        dentistRepository.deleteById(id);
    }


    // =========================
    // SEARCH BY NAME
    // =========================

    public List<DentistResponse> searchByName(
            String name
    ) {

        return dentistRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        name,
                        name
                )
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    // =========================
    // SEARCH SPECIALIZATION
    // =========================

    public List<DentistResponse> searchBySpecialization(
            String specialization
    ) {

        return dentistRepository
                .findBySpecializationContainingIgnoreCase(
                        specialization
                )
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    // =========================
    // SEARCH CLINIC
    // =========================

    public List<DentistResponse> searchByClinic(
            String clinic
    ) {

        return dentistRepository
                .findByClinicNameContainingIgnoreCase(
                        clinic
                )
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    // =========================
    // CURRENT DENTIST
    // =========================

    public DentistResponse getCurrentDentist() {

        String email =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName();

        Dentist dentist =
                dentistRepository
                        .findByUserEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No dentist profile found for this account"
                                )
                        );

        return map(dentist);
    }


    // =========================
    // DENTIST MAPPER
    // =========================

    private DentistResponse map(
            Dentist dentist
    ) {

        return DentistResponse.builder()
                .id(dentist.getId())
                .userId(
                        dentist.getUser().getId()
                )
                .firstName(
                        dentist.getFirstName()
                )
                .lastName(
                        dentist.getLastName()
                )
                .specialization(
                        dentist.getSpecialization()
                )
                .phone(
                        dentist.getPhone()
                )
                .clinicName(
                        dentist.getClinicName()
                )
                .experienceYears(
                        dentist.getExperienceYears()
                )
                .biography(
                        dentist.getBiography()
                )
                .profileImage(
                        dentist.getProfileImage()
                )
                .build();
    }


    // =========================
    // APPOINTMENT MAPPER
    // =========================

    private AppointmentResponse mapAppointment(
            Appointment appointment
    ) {

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .clientId(
                        appointment.getClient().getId()
                )
                .dentistId(
                        appointment.getDentist().getId()
                )
                .treatmentId(
                        appointment.getTreatment().getId()
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


    // =========================
    // SCHEDULE MAPPER
    // =========================

    private ScheduleResponse mapSchedule(
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