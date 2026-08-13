package com.example.dentistapp.service;


import com.example.dentistapp.dto.AppointmentRequest;
import com.example.dentistapp.dto.AppointmentResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.example.dentistapp.model.*;

import com.example.dentistapp.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.time.DayOfWeek;



@Service
@RequiredArgsConstructor
public class AppointmentService {


    private final AppointmentRepository appointmentRepository;


    private final ClientRepository clientRepository;


    private final DentistRepository dentistRepository;

    private final TreatmentRepository treatmentRepository;

    private final DentistAvailabilityRepository availabilityRepository;

    private final NotificationService notificationService;



    public AppointmentResponse create(
        AppointmentRequest request
) {

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

    Treatment treatment =
            treatmentRepository.findById(
                    request.getTreatmentId()
            )
            .orElseThrow(
                    () -> new RuntimeException("Treatment not found")
            );

    LocalDate appointmentDate =
            request.getAppointmentDate();

    LocalTime startTime =
            request.getStartTime();

    LocalTime endTime =
            startTime.plusMinutes(
                    treatment.getDurationMinutes()
            );

    DayOfWeek dayOfWeek =
            appointmentDate.getDayOfWeek();

    DentistAvailability availability =
            availabilityRepository
                    .findByDentistAndDayOfWeek(
                            dentist,
                            dayOfWeek
                    )
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "Dentist is not working on this day"
                            )
                    );

    if (startTime.isBefore(availability.getStartTime())
            || endTime.isAfter(availability.getEndTime())) {

        throw new RuntimeException(
                "Appointment is outside dentist working hours"
        );
    }

    List<Appointment> appointments =
            appointmentRepository
                    .findByDentistIdAndAppointmentDateOrderByStartTime(
                            dentist.getId(),
                            appointmentDate
                    );

    boolean occupied =
            appointments.stream()
                    .anyMatch(appointment ->
                            !"REJECTED".equals(
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

notificationService.create(
        appointment.getClient().getUser(),
        "Your appointment has been accepted."
);

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

notificationService.create(
        appointment.getClient().getUser(),
        "Your appointment has been rejected."
);

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

public List<LocalTime> getAvailableSlots(
        Long dentistId,
        LocalDate date,
        Long treatmentId
) {

    Dentist dentist =
            dentistRepository.findById(dentistId)
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "Dentist not found"
                            )
                    );

    Treatment treatment =
            treatmentRepository.findById(treatmentId)
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "Treatment not found"
                            )
                    );

    DayOfWeek dayOfWeek = date.getDayOfWeek();

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

    List<LocalTime> availableSlots = new ArrayList<>();

    LocalTime current =
            availability.getStartTime();

    LocalTime workingEnd =
            availability.getEndTime();

    int duration =
            treatment.getDurationMinutes();

    while (!current.plusMinutes(duration).isAfter(workingEnd)) {

    LocalTime slotStart = current;
    LocalTime slotEnd = current.plusMinutes(duration);

    boolean occupied = appointments.stream()
            .anyMatch(appointment ->
                    !"REJECTED".equals(appointment.getStatus())
                    &&
                    slotStart.isBefore(appointment.getEndTime())
                    &&
                    slotEnd.isAfter(appointment.getStartTime())
            );

    if (!occupied) {
        availableSlots.add(slotStart);
    }

    current = current.plusMinutes(30);
}

    return availableSlots;
}

}