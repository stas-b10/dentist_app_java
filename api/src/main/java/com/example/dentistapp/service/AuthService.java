package com.example.dentistapp.service;

import com.example.dentistapp.dto.AuthResponse;
import com.example.dentistapp.dto.ClientResponse;
import com.example.dentistapp.dto.DentistResponse;
import com.example.dentistapp.dto.LoginRequest;
import com.example.dentistapp.dto.MeResponse;
import com.example.dentistapp.dto.RegisterRequest;

import com.example.dentistapp.model.Client;
import com.example.dentistapp.model.Dentist;
import com.example.dentistapp.model.Role;
import com.example.dentistapp.model.User;

import com.example.dentistapp.repository.ClientRepository;
import com.example.dentistapp.repository.DentistRepository;
import com.example.dentistapp.repository.UserRepository;

import com.example.dentistapp.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final DentistRepository dentistRepository;
    private final JwtService jwtService;


    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }

        if (request.getRole() == null) {
            throw new RuntimeException("Role is required");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail().trim().toLowerCase())
                .password(request.getPassword())
                .role(request.getRole())
                .build();

        userRepository.save(user);

        /*
         * Create an empty profile automatically.
         *
         * The frontend can later update the profile
         * with first name, last name, phone, etc.
         */
        if (user.getRole() == Role.CLIENT) {

            Client client = Client.builder()
                    .user(user)
                    .build();

            clientRepository.save(client);

        } else if (user.getRole() == Role.DENTIST) {

            Dentist dentist = Dentist.builder()
                    .user(user)
                    .build();

            dentistRepository.save(dentist);
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }


    public AuthResponse login(LoginRequest request) {

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new RuntimeException("Email is required");
        }

        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new RuntimeException("Password is required");
        }

        User user = userRepository
                .findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() ->
                        new RuntimeException("Email not found")
                );

        if (!request.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);
    }


    public MeResponse getMe() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            throw new RuntimeException(
                    "User is not authenticated"
            );
        }

        String email = authentication.getName();
        
        System.out.println(
        "AUTHENTICATION NAME = [" + authentication.getName() + "]"
);

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        )
                );

        MeResponse.MeResponseBuilder builder =
                MeResponse.builder()
                        .userId(user.getId())
                        .email(user.getEmail())
                        .role(user.getRole().name());

        if (user.getRole() == Role.CLIENT) {

            clientRepository
                    .findByUser(user)
                    .ifPresent(client ->
                            builder.client(
                                    mapClient(client)
                            )
                    );

        } else if (user.getRole() == Role.DENTIST) {

            dentistRepository
                    .findByUser(user)
                    .ifPresent(dentist ->
                            builder.dentist(
                                    mapDentist(dentist)
                            )
                    );
        }

        return builder.build();
    }


    private ClientResponse mapClient(Client client) {

        return ClientResponse.builder()
                .id(client.getId())
                .userId(client.getUser().getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .phone(client.getPhone())
                .dateOfBirth(client.getDateOfBirth())
                .profileImage(client.getProfileImage())
                .build();
    }


    private DentistResponse mapDentist(Dentist dentist) {

        return DentistResponse.builder()
                .id(dentist.getId())
                .userId(dentist.getUser().getId())
                .firstName(dentist.getFirstName())
                .lastName(dentist.getLastName())
                .specialization(dentist.getSpecialization())
                .phone(dentist.getPhone())
                .clinicName(dentist.getClinicName())
                .experienceYears(dentist.getExperienceYears())
                .biography(dentist.getBiography())
                .profileImage(dentist.getProfileImage())
                .build();
    }
}