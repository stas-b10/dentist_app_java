package com.example.dentistapp.repository;

import com.example.dentistapp.model.DentistTreatment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DentistTreatmentRepository
        extends JpaRepository<DentistTreatment, Long> {

    List<DentistTreatment> findByDentistId(Long dentistId);

    List<DentistTreatment> findByTreatmentId(Long treatmentId);

    boolean existsByDentistIdAndTreatmentId(
            Long dentistId,
            Long treatmentId
    );
}