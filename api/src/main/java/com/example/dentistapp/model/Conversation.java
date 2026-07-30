package com.example.dentistapp.model;


import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;



@Entity
@Table(name="conversations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne
    @JoinColumn(name="client_id")
    private User client;



    @ManyToOne
    @JoinColumn(name="dentist_id")
    private User dentist;



    private LocalDateTime createdAt;


}