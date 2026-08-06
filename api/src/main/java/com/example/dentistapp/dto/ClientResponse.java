package com.example.dentistapp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClientResponse {

    private Long id;

    private Long userId;

    private String firstName;

    private String lastName;

    private String phone;

    private String dateOfBirth;

    private String profileImage;

}