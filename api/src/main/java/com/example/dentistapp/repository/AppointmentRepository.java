package com.example.dentistapp.repository;

import java.util.Optional;
import com.example.dentistapp.model.Appointment;

import org.springframework.data.jpa.repository.JpaRepository;


public interface AppointmentRepository 
        extends JpaRepository<Appointment,Long> {

            Optional<Appointment> findByIdAndDentistId(Long id, Long dentistId);
}