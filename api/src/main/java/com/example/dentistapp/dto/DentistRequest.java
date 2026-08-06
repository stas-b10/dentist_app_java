package com.example.dentistapp.dto;

import lombok.Data;

@Data
public class DentistRequest {

    private Long userId;

    private String firstName;

    private String lastName;

    private String specialization;

    private String phone;

    private String clinicName;

    private Integer experienceYears;

    private String biography;
    
    private String profileImage;

}