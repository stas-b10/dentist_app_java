package com.example.dentistapp.repository;

import com.example.dentistapp.model.Appointment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDentistIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
            Long dentistId,
            String status
    );

    List<Appointment> findByDentistId(
            Long dentistId
    );

    List<Appointment> findByClientIdOrderByAppointmentDateDescStartTimeDesc(
            Long clientId
    );

    Optional<Appointment> findByIdAndDentistId(
            Long appointmentId,
            Long dentistId
    );

    List<Appointment> findByDentistIdAndAppointmentDateOrderByStartTime(
            Long dentistId,
            LocalDate date
    );

    long countByDentistId(
            Long dentistId
    );

    long countByDentistIdAndStatus(
            Long dentistId,
            String status
    );
}