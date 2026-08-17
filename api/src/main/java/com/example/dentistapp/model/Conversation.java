package com.example.dentistapp.model;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "conversations",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"client_id", "dentist_id"}
        )
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private User client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "dentist_id", nullable = false)
    private User dentist;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}