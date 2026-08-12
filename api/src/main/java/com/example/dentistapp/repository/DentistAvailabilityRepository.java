package com.example.dentistapp.repository;

import com.example.dentistapp.model.DentistAvailability;
import com.example.dentistapp.model.Dentist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

public interface DentistAvailabilityRepository
        extends JpaRepository<DentistAvailability, Long> {

    List<DentistAvailability> findByDentist(
            Dentist dentist
    );

    Optional<DentistAvailability> findByDentistAndDayOfWeek(
            Dentist dentist,
            DayOfWeek dayOfWeek
    );

    void deleteByDentist(Dentist dentist);
}