package com.example.dentistapp.repository;


import com.example.dentistapp.model.Treatment;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface TreatmentRepository 
        extends JpaRepository<Treatment, Long> {


    List<Treatment> findByClientId(Long clientId);


    List<Treatment> findByDentistId(Long dentistId);


}