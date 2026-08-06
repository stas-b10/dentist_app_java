package com.example.dentistapp.repository;


import com.example.dentistapp.model.Schedule;

import org.springframework.data.jpa.repository.JpaRepository;


public interface ScheduleRepository 
        extends JpaRepository<Schedule, Long> {


}