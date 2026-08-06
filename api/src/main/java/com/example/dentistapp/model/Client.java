package com.example.dentistapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clients")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Client {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;


    @Column(nullable = false)
    private String firstName;


    @Column(nullable = false)
    private String lastName;


    private String phone;


    private String dateOfBirth;


    private String profileImage;

}