package com.example.dentistapp.repository;

import com.example.dentistapp.model.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TreatmentRepository
        extends JpaRepository<Treatment, Long> {

    Optional<Treatment> findByName(String name);
}