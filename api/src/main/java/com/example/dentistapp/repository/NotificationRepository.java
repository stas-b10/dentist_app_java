package com.example.dentistapp.repository;

import com.example.dentistapp.model.Notification;
import com.example.dentistapp.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(
            User user
    );

    List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(
            User user
    );
}