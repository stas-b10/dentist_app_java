package com.example.dentistapp.repository;

import com.example.dentistapp.model.Dentist;
import com.example.dentistapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DentistRepository extends JpaRepository<Dentist, Long> {

    Optional<Dentist> findByUser(User user);

    Optional<Dentist> findByUserEmail(String email);

    List<Dentist> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName,
            String lastName
    );

    List<Dentist> findBySpecializationContainingIgnoreCase(
            String specialization
    );

    List<Dentist> findByClinicNameContainingIgnoreCase(
            String clinicName
    );
}