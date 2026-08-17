package com.example.dentistapp.service;

import com.example.dentistapp.dto.AppointmentRequest;
import com.example.dentistapp.dto.AppointmentResponse;

import com.example.dentistapp.model.Appointment;
import com.example.dentistapp.model.Client;
import com.example.dentistapp.model.Dentist;
import com.example.dentistapp.model.DentistAvailability;
import com.example.dentistapp.model.Treatment;

import com.example.dentistapp.repository.AppointmentRepository;
import com.example.dentistapp.repository.ClientRepository;
import com.example.dentistapp.repository.DentistAvailabilityRepository;
import com.example.dentistapp.repository.DentistRepository;
import com.example.dentistapp.repository.TreatmentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final ClientRepository clientRepository;

    private final DentistRepository dentistRepository;

    private final TreatmentRepository treatmentRepository;

    private final DentistAvailabilityRepository availabilityRepository;

    private final NotificationService notificationService;


    @Transactional
    public AppointmentResponse create(
            AppointmentRequest request
    ) {

        /*
         * IMPORTANT:
         * We get the client from the JWT instead of trusting
         * clientId sent by the frontend.
         */
        Client client = getAuthenticatedClient();

        Dentist dentist =
                dentistRepository
                        .findById(request.getDentistId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dentist not found"
                                )
                        );

        Treatment treatment =
                treatmentRepository
                        .findById(request.getTreatmentId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Treatment not found"
                                )
                        );

        if (request.getAppointmentDate() == null) {
            throw new RuntimeException(
                    "Appointment date is required"
            );
        }

        if (request.getStartTime() == null) {
            throw new RuntimeException(
                    "Start time is required"
            );
        }

        LocalDate appointmentDate =
                request.getAppointmentDate();

        LocalTime startTime =
                request.getStartTime();

        LocalTime endTime =
                startTime.plusMinutes(
                        treatment.getDurationMinutes()
                );


        /*
         * Do not allow appointments in the past.
         */
        if (appointmentDate.isBefore(LocalDate.now())) {
            throw new RuntimeException(
                    "Cannot create an appointment in the past"
            );
        }


        /*
         * If appointment is today, also prevent
         * choosing a time that already passed.
         */
        if (appointmentDate.equals(LocalDate.now())
                && startTime.isBefore(LocalTime.now())) {

            throw new RuntimeException(
                    "Cannot create an appointment in the past"
            );
        }


        DayOfWeek dayOfWeek =
                appointmentDate.getDayOfWeek();


        DentistAvailability availability =
                availabilityRepository
                        .findByDentistAndDayOfWeek(
                                dentist,
                                dayOfWeek
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dentist is not working on this day"
                                )
                        );


        /*
         * Check working hours.
         */
        if (startTime.isBefore(
                availability.getStartTime()
        )
                ||
                endTime.isAfter(
                        availability.getEndTime()
                )) {

            throw new RuntimeException(
                    "Appointment is outside dentist working hours"
            );
        }


        /*
         * Find appointments for that dentist and date.
         */
        List<Appointment> appointments =
                appointmentRepository
                        .findByDentistIdAndAppointmentDateOrderByStartTime(
                                dentist.getId(),
                                appointmentDate
                        );


        /*
         * Check overlapping appointments.
         */
        boolean occupied =
                appointments.stream()
                        .anyMatch(appointment ->

                                !"REJECTED".equals(
                                        appointment.getStatus()
                                )
                                &&
                                !"CANCELLED".equals(
                                        appointment.getStatus()
                                )
                                &&
                                startTime.isBefore(
                                        appointment.getEndTime()
                                )
                                &&
                                endTime.isAfter(
                                        appointment.getStartTime()
                                )
                        );


        if (occupied) {
            throw new RuntimeException(
                    "This time slot is already occupied"
            );
        }


        Appointment appointment =
                Appointment.builder()
                        .client(client)
                        .dentist(dentist)
                        .treatment(treatment)
                        .appointmentDate(appointmentDate)
                        .startTime(startTime)
                        .endTime(endTime)
                        .status("PENDING")
                        .build();


        appointmentRepository.save(appointment);


        notificationService.create(
                dentist.getUser(),
                "You have a new appointment request."
        );


        return map(appointment);
    }


    public List<AppointmentResponse> getAll() {

        return appointmentRepository
                .findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    public List<AppointmentResponse> getDentistPendingRequests() {

        Dentist dentist =
                getAuthenticatedDentist();

        return appointmentRepository
                .findByDentistIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
                        dentist.getId(),
                        "PENDING"
                )
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    @Transactional
    public AppointmentResponse accept(
            Long appointmentId
    ) {

        Dentist dentist =
                getAuthenticatedDentist();

        Appointment appointment =
                appointmentRepository
                        .findByIdAndDentistId(
                                appointmentId,
                                dentist.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Appointment not found"
                                )
                        );


        if (!"PENDING".equals(
                appointment.getStatus()
        )) {

            throw new RuntimeException(
                    "Appointment is not pending"
            );
        }


        appointment.setStatus("ACCEPTED");

        appointmentRepository.save(appointment);


        notificationService.create(
                appointment.getClient().getUser(),
                "Your appointment has been accepted."
        );


        return map(appointment);
    }


    @Transactional
    public AppointmentResponse reject(
            Long appointmentId
    ) {

        Dentist dentist =
                getAuthenticatedDentist();

        Appointment appointment =
                appointmentRepository
                        .findByIdAndDentistId(
                                appointmentId,
                                dentist.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Appointment not found"
                                )
                        );


        if (!"PENDING".equals(
                appointment.getStatus()
        )) {

            throw new RuntimeException(
                    "Appointment is not pending"
            );
        }


        appointment.setStatus("REJECTED");

        appointmentRepository.save(appointment);


        notificationService.create(
                appointment.getClient().getUser(),
                "Your appointment has been rejected."
        );


        return map(appointment);
    }


    public List<AppointmentResponse> getMyAppointments() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        String role =
                authentication
                        .getAuthorities()
                        .stream()
                        .findFirst()
                        .map(Object::toString)
                        .orElse("");


        if (role.contains("CLIENT")) {

            Client client =
                    getAuthenticatedClient();

            return appointmentRepository
                    .findByClientIdOrderByAppointmentDateDescStartTimeDesc(
                            client.getId()
                    )
                    .stream()
                    .map(this::map)
                    .collect(Collectors.toList());
        }


        Dentist dentist =
                getAuthenticatedDentist();

        return appointmentRepository
                .findByDentistId(dentist.getId())
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    @Transactional
    public AppointmentResponse cancel(
            Long appointmentId
    ) {

        Client client =
                getAuthenticatedClient();


        Appointment appointment =
                appointmentRepository
                        .findByIdAndClientId(
                                appointmentId,
                                client.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Appointment not found"
                                )
                        );


        if (!"PENDING".equals(
                appointment.getStatus()
        )) {

            throw new RuntimeException(
                    "Only pending appointments can be cancelled"
            );
        }


        appointment.setStatus("CANCELLED");

        appointmentRepository.save(appointment);


        notificationService.create(
                appointment.getDentist().getUser(),
                "A client cancelled their appointment request."
        );


        return map(appointment);
    }


    @Transactional
    public AppointmentResponse complete(
            Long appointmentId
    ) {

        Dentist dentist =
                getAuthenticatedDentist();


        Appointment appointment =
                appointmentRepository
                        .findByIdAndDentistId(
                                appointmentId,
                                dentist.getId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Appointment not found"
                                )
                        );


        if (!"ACCEPTED".equals(
                appointment.getStatus()
        )) {

            throw new RuntimeException(
                    "Only accepted appointments can be completed"
            );
        }


        appointment.setStatus("COMPLETED");

        appointmentRepository.save(appointment);


        notificationService.create(
                appointment.getClient().getUser(),
                "Your appointment has been marked as completed."
        );


        return map(appointment);
    }


    public List<LocalTime> getAvailableSlots(
            Long dentistId,
            LocalDate date,
            Long treatmentId
    ) {

        Dentist dentist =
                dentistRepository
                        .findById(dentistId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Dentist not found"
                                )
                        );


        Treatment treatment =
                treatmentRepository
                        .findById(treatmentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Treatment not found"
                                )
                        );


        DayOfWeek dayOfWeek =
                date.getDayOfWeek();


        DentistAvailability availability =
                availabilityRepository
                        .findByDentistAndDayOfWeek(
                                dentist,
                                dayOfWeek
                        )
                        .orElse(null);


        if (availability == null) {
            return List.of();
        }


        List<Appointment> appointments =
                appointmentRepository
                        .findByDentistIdAndAppointmentDateOrderByStartTime(
                                dentistId,
                                date
                        );


        List<LocalTime> availableSlots =
                new ArrayList<>();


        LocalTime current =
                availability.getStartTime();


        LocalTime workingEnd =
                availability.getEndTime();


        int duration =
                treatment.getDurationMinutes();


        while (!current
                .plusMinutes(duration)
                .isAfter(workingEnd)) {

            LocalTime slotStart =
                    current;

            LocalTime slotEnd =
                    current.plusMinutes(duration);


            boolean occupied =
                    appointments.stream()
                            .anyMatch(appointment ->

                                    !"REJECTED".equals(
                                            appointment.getStatus()
                                    )
                                    &&
                                    !"CANCELLED".equals(
                                            appointment.getStatus()
                                    )
                                    &&
                                    slotStart.isBefore(
                                            appointment.getEndTime()
                                    )
                                    &&
                                    slotEnd.isAfter(
                                            appointment.getStartTime()
                                    )
                            );


            boolean inPast =
                    date.equals(LocalDate.now())
                    &&
                    !slotStart.isAfter(
                            LocalTime.now()
                    );


            if (!occupied && !inPast) {
                availableSlots.add(slotStart);
            }


            /*
             * Slots are generated every 30 minutes.
             */
            current =
                    current.plusMinutes(30);
        }


        return availableSlots;
    }


    private AppointmentResponse map(
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


    private Dentist getAuthenticatedDentist() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }


        String email =
                authentication.getName();


        return dentistRepository
                .findByUserEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Dentist profile not found"
                        )
                );
    }


    private Client getAuthenticatedClient() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();


        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }


        String email =
                authentication.getName();


        return clientRepository
                .findByUserEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Client profile not found"
                        )
                );
    }
}