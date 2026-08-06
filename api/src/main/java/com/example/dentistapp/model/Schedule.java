package com.example.dentistapp.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "schedules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;


    @ManyToOne
    @JoinColumn(name = "dentist_id", nullable = false)
    private Dentist dentist;


    private LocalDateTime appointmentDate;


    private String status;


}