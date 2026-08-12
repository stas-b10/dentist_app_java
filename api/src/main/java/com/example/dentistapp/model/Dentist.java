package com.example.dentistapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dentists")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dentist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String firstName;
    private String lastName;
    private String specialization;
    private String phone;
    private String clinicName;
    private Integer experienceYears;

    @Column(length = 1000)
    private String biography;

    private String profileImage;
}