package com.example.dentistapp.controller;


import com.example.dentistapp.dto.AuthResponse;
import com.example.dentistapp.dto.LoginRequest;
import com.example.dentistapp.dto.MeResponse;
import com.example.dentistapp.dto.RegisterRequest;
import com.example.dentistapp.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(
            @RequestBody RegisterRequest request
    ){

        return authService.register(request);

    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request
    ){

        return authService.login(request);

    }

    @GetMapping("/me")
    public MeResponse me() {

        return authService.getMe();

    }


}