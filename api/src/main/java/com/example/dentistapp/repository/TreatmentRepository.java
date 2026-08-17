package com.example.dentistapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.dentistapp.model.Treatment;

public interface TreatmentRepository
        extends JpaRepository<Treatment, Long> {
}