package com.example.dentistapp.dto;


import lombok.Data;


@Data
public class TreatmentRequest {


    private Long clientId;


    private Long dentistId;


    private String title;


    private String description;


}