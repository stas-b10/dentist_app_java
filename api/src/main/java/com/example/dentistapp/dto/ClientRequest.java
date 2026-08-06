package com.example.dentistapp.dto;

import lombok.Data;

@Data
public class ClientRequest {

    private Long userId;

    private String firstName;

    private String lastName;

    private String phone;

    private String dateOfBirth;

    private String profileImage;

}