package com.example.dentistapp.repository;

import com.example.dentistapp.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByIdAndDentistId(
            Long id,
            Long dentistId
    );

    List<Appointment> findByClientIdOrderByAppointmentDateDescStartTimeDesc(
            Long clientId
    );

    List<Appointment> findByDentistIdOrderByAppointmentDateDescStartTimeDesc(
            Long dentistId
    );

    List<Appointment> findByDentistIdAndAppointmentDateOrderByStartTime(
            Long dentistId,
            LocalDate appointmentDate
    );

    List<Appointment> findByDentistIdAndStatusOrderByAppointmentDateAscStartTimeAsc(
            Long dentistId,
            String status
    );

    long countByDentistIdAndStatus(
            Long dentistId,
            String status
    );

    long countByClientIdAndStatus(
            Long clientId,
            String status
    );
    
}