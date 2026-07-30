package com.example.dentistapp.repository;


import com.example.dentistapp.model.Conversation;

import com.example.dentistapp.model.User;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;



public interface ConversationRepository 
        extends JpaRepository<Conversation,Long> {


    Optional<Conversation> 
    findByClientAndDentist(
            User client,
            User dentist
    );


}