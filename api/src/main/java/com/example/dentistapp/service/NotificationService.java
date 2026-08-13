package com.example.dentistapp.service;

import com.example.dentistapp.dto.NotificationResponse;
import com.example.dentistapp.model.Notification;
import com.example.dentistapp.model.User;
import com.example.dentistapp.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;


    public NotificationResponse create(
            User user,
            String message
    ) {

        Notification notification =
                Notification.builder()
                        .user(user)
                        .message(message)
                        .read(false)
                        .createdAt(LocalDateTime.now())
                        .build();

        notificationRepository.save(notification);

        return map(notification);
    }


    public List<NotificationResponse> getUserNotifications(
            User user
    ) {

        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::map)
                .toList();
    }


    public List<NotificationResponse> getUnreadNotifications(
            User user
    ) {

        return notificationRepository
                .findByUserAndReadFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(this::map)
                .toList();
    }


    public void markAsRead(
            Long notificationId,
            User user
    ) {

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Notification not found"
                                )
                        );

        if (!notification.getUser().getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "You cannot access this notification"
            );
        }

        notification.setRead(true);

        notificationRepository.save(notification);
    }


    private NotificationResponse map(
            Notification notification
    ) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}