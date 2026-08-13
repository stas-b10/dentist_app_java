package com.example.dentistapp.controller;

import com.example.dentistapp.dto.NotificationResponse;
import com.example.dentistapp.model.User;
import com.example.dentistapp.repository.UserRepository;
import com.example.dentistapp.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    private final UserRepository userRepository;


    @GetMapping
    public List<NotificationResponse> getNotifications(
            Authentication authentication
    ) {

        User user = getAuthenticatedUser(authentication);

        return notificationService
                .getUserNotifications(user);
    }


    @GetMapping("/unread")
    public List<NotificationResponse> getUnreadNotifications(
            Authentication authentication
    ) {

        User user = getAuthenticatedUser(authentication);

        return notificationService
                .getUnreadNotifications(user);
    }


    @PutMapping("/{notificationId}/read")
    public void markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {

        User user = getAuthenticatedUser(authentication);

        notificationService.markAsRead(
                notificationId,
                user
        );
    }


    private User getAuthenticatedUser(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );
    }
}