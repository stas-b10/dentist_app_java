package com.example.dentistapp.dto;

import com.example.dentistapp.model.Role;
import lombok.Data;


@Data
public class RegisterRequest {

    private String email;
    private String password;
    private Role role;

}