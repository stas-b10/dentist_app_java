package com.example.dentistapp.repository;

import com.example.dentistapp.model.MedicalRecord;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByClientIdOrderByCreatedAtDesc(
            Long clientId
    );

    List<MedicalRecord> findByDentistIdOrderByCreatedAtDesc(
            Long dentistId
    );
}