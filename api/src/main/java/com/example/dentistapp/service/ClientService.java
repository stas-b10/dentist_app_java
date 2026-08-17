package com.example.dentistapp.service;

import com.example.dentistapp.dto.AppointmentResponse;
import com.example.dentistapp.dto.ClientRequest;
import com.example.dentistapp.dto.ClientResponse;
import com.example.dentistapp.model.Appointment;
import com.example.dentistapp.model.Client;
import com.example.dentistapp.model.User;
import com.example.dentistapp.repository.AppointmentRepository;
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
    private final AppointmentRepository appointmentRepository;


    // =========================
    // CREATE
    // =========================

    public ClientResponse create(ClientRequest request) {

        User user = userRepository.findById(request.getUserId())
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

        Client savedClient = clientRepository.save(client);

        return map(savedClient);
    }


    // =========================
    // GET ALL
    // =========================

    public List<ClientResponse> getAll() {

        return clientRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    // =========================
    // GET BY ID
    // =========================

    public ClientResponse getById(Long id) {

        Client client = clientRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("Client not found")
                );

        return map(client);
    }


    // =========================
    // GET APPOINTMENTS
    // =========================

    public List<AppointmentResponse> getAppointments(
            Long clientId
    ) {

        // Make sure the client actually exists.
        clientRepository.findById(clientId)
                .orElseThrow(
                        () -> new RuntimeException("Client not found")
                );

        return appointmentRepository
                .findByClientIdOrderByAppointmentDateDescStartTimeDesc(
                        clientId
                )
                .stream()
                .map(this::mapAppointment)
                .collect(Collectors.toList());
    }


    // =========================
    // DELETE
    // =========================

    public void delete(Long id) {

        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Client not found");
        }

        clientRepository.deleteById(id);
    }


    // =========================
    // CLIENT MAPPER
    // =========================

    private ClientResponse map(Client client) {

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


    // =========================
    // APPOINTMENT MAPPER
    // =========================

    private AppointmentResponse mapAppointment(
            Appointment appointment
    ) {

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .clientId(
                        appointment.getClient().getId()
                )
                .dentistId(
                        appointment.getDentist().getId()
                )
                .treatmentId(
                        appointment.getTreatment().getId()
                )
                .appointmentDate(
                        appointment.getAppointmentDate()
                )
                .startTime(
                        appointment.getStartTime()
                )
                .endTime(
                        appointment.getEndTime()
                )
                .status(
                        appointment.getStatus()
                )
                .build();
    }
}