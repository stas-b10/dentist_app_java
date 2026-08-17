package com.example.dentistapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println();
        System.out.println("=================================");
        System.out.println("JWT FILTER");

        System.out.println(
                "Request: "
                        + request.getMethod()
                        + " "
                        + request.getRequestURI()
        );


        String authHeader =
                request.getHeader("Authorization");


        System.out.println(
                "Authorization header present: "
                        + (authHeader != null)
        );


        /*
         * No Authorization header.
         */
        if (
                authHeader == null
                || !authHeader.startsWith("Bearer ")
        ) {

            System.out.println(
                    "No valid Bearer token found"
            );

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        /*
         * Extract JWT.
         */
        String token =
                authHeader
                        .substring(7)
                        .trim();


        if (token.isEmpty()) {

            System.out.println(
                    "Bearer token is empty"
            );

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }


        try {

            /*
             * Get email from JWT.
             */
            String username =
                    jwtService.extractUsername(
                            token
                    );


            System.out.println(
                    "JWT username/email = "
                            + username
            );


            if (
                    username != null
                    && !username.isBlank()
                    && SecurityContextHolder
                            .getContext()
                            .getAuthentication()
                            == null
            ) {

                /*
                 * IMPORTANT:
                 *
                 * Your JwtService has:
                 *
                 * isTokenValid(String, String)
                 *
                 * therefore we pass username here,
                 * NOT UserDetails.
                 */
                boolean valid =
                        jwtService.isTokenValid(
                                token,
                                username
                        );


                System.out.println(
                        "JWT valid = "
                                + valid
                );


                if (!valid) {

                    System.out.println(
                            "JWT is invalid or expired"
                    );

                    SecurityContextHolder
                            .clearContext();

                    filterChain.doFilter(
                            request,
                            response
                    );

                    return;
                }


                /*
                 * Load user from database.
                 */
                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(
                                        username
                                );


                System.out.println(
                        "User loaded successfully: "
                                + userDetails.getUsername()
                );


                /*
                 * Create Spring Security authentication.
                 */
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );


                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );


                /*
                 * Store authentication.
                 */
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authentication
                        );


                System.out.println(
                        "JWT authentication SUCCESS"
                );


                System.out.println(
                        "Authenticated user = "
                                + authentication.getName()
                );


                System.out.println(
                        "Authorities = "
                                + authentication.getAuthorities()
                );
            }

        } catch (Exception e) {

            /*
             * JWT parsing/validation failed.
             */
            SecurityContextHolder
                    .clearContext();


            System.out.println(
                    "JWT authentication FAILED"
            );


            System.out.println(
                    "Reason: "
                            + e.getMessage()
            );
        }


        System.out.println(
                "Authentication before controller = "
                        + SecurityContextHolder
                                .getContext()
                                .getAuthentication()
        );


        System.out.println(
                "================================="
        );
        System.out.println();


        filterChain.doFilter(
                request,
                response
        );
    }
}