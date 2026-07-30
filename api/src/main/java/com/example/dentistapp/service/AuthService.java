package com.example.dentistapp.service;


import com.example.dentistapp.dto.AuthResponse;
import com.example.dentistapp.dto.LoginRequest;
import com.example.dentistapp.dto.RegisterRequest;

import com.example.dentistapp.model.User;
import com.example.dentistapp.repository.UserRepository;
import com.example.dentistapp.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthResponse register(
            RegisterRequest request
    ){
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .build();

        userRepository.save(user);

        String token =
                jwtService.generateToken(user);
        return new AuthResponse(token);

    }

    public AuthResponse login(
            LoginRequest request
    ){
        User user =
                userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(
                    () -> new RuntimeException(
                        "Email not found"
                    )
                );

        if(!user.getPassword()
                .equals(request.getPassword())){
            throw new RuntimeException(
                    "Wrong password"
            );
        }
        String token =
                jwtService.generateToken(user);
        return new AuthResponse(token);

    }


}