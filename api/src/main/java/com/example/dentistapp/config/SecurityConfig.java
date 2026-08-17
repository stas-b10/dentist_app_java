package com.example.dentistapp.config;

import com.example.dentistapp.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(
            org.springframework.security.config.annotation.web.builders.HttpSecurity http
    ) throws Exception {

        http
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                .csrf(csrf ->
                        csrf.disable()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )



                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register"
                        ).permitAll()

                        .requestMatchers(
                                "/api/auth/me"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/dentists"
                        ).authenticated()


                        .requestMatchers(
                                "/api/dentists/**"
                        ).hasRole("DENTIST")


                        .requestMatchers(
                                "/api/schedules/**"
                        ).hasRole("DENTIST")


  

                        .requestMatchers(
                                "/api/conversations/**"
                        ).authenticated()


  

                        .requestMatchers(
                                "/api/chat/**"
                        ).authenticated()


                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/medical-records/client/**"
                        ).authenticated()



                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/medical-records/dentist/**"
                        ).hasRole("DENTIST")


                        // -------------------------------------------------
                        // GET MEDICAL RECORD BY ID
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/medical-records/**"
                        ).authenticated()


                        // -------------------------------------------------
                        // CREATE MEDICAL RECORD
                        // -------------------------------------------------

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/medical-records"
                        ).hasRole("DENTIST")



                        .anyRequest().authenticated()
                )


                .exceptionHandling(exception -> exception

                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    System.out.println();
                                    System.out.println(
                                            "========================================="
                                    );
                                    System.out.println(
                                            "401 UNAUTHORIZED"
                                    );
                                    System.out.println(
                                            "========================================="
                                    );

                                    System.out.println(
                                            "Request = "
                                                    + request.getMethod()
                                                    + " "
                                                    + request.getRequestURI()
                                    );

                                    System.out.println(
                                            "Message = "
                                                    + authException.getMessage()
                                    );

                                    System.out.println(
                                            "========================================="
                                    );

                                    response.sendError(
                                            401,
                                            "Unauthorized"
                                    );
                                }
                        )


                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {

                                    System.out.println();
                                    System.out.println(
                                            "========================================="
                                    );
                                    System.out.println(
                                            "403 ACCESS DENIED"
                                    );
                                    System.out.println(
                                            "========================================="
                                    );

                                    System.out.println(
                                            "Request = "
                                                    + request.getMethod()
                                                    + " "
                                                    + request.getRequestURI()
                                    );

                                    System.out.println(
                                            "Message = "
                                                    + accessDeniedException.getMessage()
                                    );

                                    if (request.getUserPrincipal() != null) {

                                        System.out.println(
                                                "Principal = "
                                                        + request
                                                        .getUserPrincipal()
                                                        .getName()
                                        );

                                    } else {

                                        System.out.println(
                                                "Principal = NULL"
                                        );
                                    }

                                    var auth =
                                            org.springframework.security
                                                    .core.context
                                                    .SecurityContextHolder
                                                    .getContext()
                                                    .getAuthentication();

                                    if (auth != null) {

                                        System.out.println(
                                                "Authenticated = "
                                                        + auth.isAuthenticated()
                                        );

                                        System.out.println(
                                                "Authorities = "
                                                        + auth.getAuthorities()
                                        );

                                    } else {

                                        System.out.println(
                                                "Authentication = NULL"
                                        );
                                    }

                                    System.out.println(
                                            "========================================="
                                    );

                                    response.sendError(
                                            403,
                                            "Access Denied"
                                    );
                                }
                        )
                )


                // =====================================================
                // JWT FILTER
                // =====================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }


    // =============================================================
    // PASSWORD ENCODER
    // =============================================================

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }


    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();


        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:5173"
                )
        );


        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );


        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type",
                        "Accept",
                        "Origin",
                        "X-Requested-With"
                )
        );


        configuration.setExposedHeaders(
                List.of(
                        "Authorization"
                )
        );


        configuration.setAllowCredentials(true);


        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();


        source.registerCorsConfiguration(
                "/**",
                configuration
        );


        return source;
    }
}