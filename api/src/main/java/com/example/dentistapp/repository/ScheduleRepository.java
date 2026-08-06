package com.example.dentistapp.repository;


import com.example.dentistapp.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ScheduleRepository 
        extends JpaRepository<Schedule, Long> {


    List<Schedule> findByDentistId(Long dentistId);


    List<Schedule> findByDentistIdAndDayOfWeek(
            Long dentistId,
            String dayOfWeek
    );

}