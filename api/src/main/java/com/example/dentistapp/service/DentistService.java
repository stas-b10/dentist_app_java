package com.example.dentistapp.service;

import com.example.dentistapp.dto.DentistRequest;
import com.example.dentistapp.dto.DentistResponse;
import com.example.dentistapp.model.Dentist;
import com.example.dentistapp.model.User;
import com.example.dentistapp.repository.DentistRepository;
import com.example.dentistapp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DentistService {

    private final DentistRepository dentistRepository;

    private final UserRepository userRepository;

    public DentistResponse create(DentistRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Dentist dentist = Dentist.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .specialization(request.getSpecialization())
                .phone(request.getPhone())
                .clinicName(request.getClinicName())
                .experienceYears(request.getExperienceYears())
                .biography(request.getBiography())
                .profileImage(request.getProfileImage())
                .build();

        dentistRepository.save(dentist);

        return map(dentist);
    }

    public List<DentistResponse> getAll() {

        return dentistRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    public DentistResponse getById(Long id) {

        Dentist dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dentist not found"));

        return map(dentist);
    }

    public DentistResponse update(Long id, DentistRequest request) {

        Dentist dentist = dentistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dentist not found"));

        dentist.setFirstName(request.getFirstName());
        dentist.setLastName(request.getLastName());
        dentist.setSpecialization(request.getSpecialization());
        dentist.setPhone(request.getPhone());
        dentist.setClinicName(request.getClinicName());
        dentist.setExperienceYears(request.getExperienceYears());
        dentist.setBiography(request.getBiography());
        dentist.setProfileImage(request.getProfileImage());

        dentistRepository.save(dentist);

        return map(dentist);
    }

    public void delete(Long id) {

        dentistRepository.deleteById(id);

    }

    private DentistResponse map(Dentist dentist) {

        return DentistResponse.builder()
                .id(dentist.getId())
                .userId(dentist.getUser().getId())
                .firstName(dentist.getFirstName())
                .lastName(dentist.getLastName())
                .specialization(dentist.getSpecialization())
                .phone(dentist.getPhone())
                .clinicName(dentist.getClinicName())
                .experienceYears(dentist.getExperienceYears())
                .biography(dentist.getBiography())
                .profileImage(dentist.getProfileImage())
                .build();

    }

}