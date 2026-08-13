package com.example.dentistapp.repository;

import com.example.dentistapp.model.Review;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    List<Review> findByDentistIdOrderByCreatedAtDesc(
            Long dentistId
    );

    List<Review> findByClientIdOrderByCreatedAtDesc(
            Long clientId
    );

    Optional<Review> findByAppointmentId(
            Long appointmentId
    );

    boolean existsByAppointmentId(
            Long appointmentId
    );

    List<Review> findByDentistId(Long dentistId);
}