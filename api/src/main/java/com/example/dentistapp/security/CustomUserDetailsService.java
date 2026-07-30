package com.example.dentistapp.security;


import com.example.dentistapp.model.User;
import com.example.dentistapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class CustomUserDetailsService 
        implements UserDetailsService {


    private final UserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String email) {


        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );


        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();

    }

}