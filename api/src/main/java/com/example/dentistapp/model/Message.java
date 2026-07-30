package com.example.dentistapp.model;


import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;



@Entity
@Table(name="messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne
    @JoinColumn(name="conversation_id")
    private Conversation conversation;



    @ManyToOne
    @JoinColumn(name="sender_id")
    private User sender;



    @Column(columnDefinition = "TEXT")
    private String content;



    private LocalDateTime sentAt;



}