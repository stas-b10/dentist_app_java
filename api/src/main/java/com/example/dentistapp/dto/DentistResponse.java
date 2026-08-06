package com.example.dentistapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DentistResponse {

    private Long id;

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