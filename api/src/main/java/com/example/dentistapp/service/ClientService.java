package com.example.dentistapp.service;


import com.example.dentistapp.dto.ClientRequest;
import com.example.dentistapp.dto.ClientResponse;
import com.example.dentistapp.model.Client;
import com.example.dentistapp.model.User;
import com.example.dentistapp.repository.ClientRepository;
import com.example.dentistapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ClientService {


    private final ClientRepository clientRepository;

    private final UserRepository userRepository;



    public ClientResponse create(ClientRequest request){


        User user =
                userRepository.findById(request.getUserId())
                .orElseThrow(
                    () -> new RuntimeException("User not found")
                );


        Client client = Client.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .dateOfBirth(request.getDateOfBirth())
                .profileImage(request.getProfileImage())
                .build();


        clientRepository.save(client);


        return map(client);

    }



    public List<ClientResponse> getAll(){

        return clientRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());

    }



    public ClientResponse getById(Long id){


        Client client =
                clientRepository.findById(id)
                .orElseThrow(
                    () -> new RuntimeException("Client not found")
                );


        return map(client);

    }



    public void delete(Long id){

        clientRepository.deleteById(id);

    }




    private ClientResponse map(Client client){

        return ClientResponse.builder()
                .id(client.getId())
                .userId(client.getUser().getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .phone(client.getPhone())
                .dateOfBirth(client.getDateOfBirth())
                .profileImage(client.getProfileImage())
                .build();

    }

}