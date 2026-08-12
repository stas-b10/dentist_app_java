package com.example.dentistapp.service;

import com.example.dentistapp.dto.TreatmentRequest;
import com.example.dentistapp.dto.TreatmentResponse;
import com.example.dentistapp.model.Treatment;
import com.example.dentistapp.repository.TreatmentRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TreatmentService {

    private final TreatmentRepository treatmentRepository;

    public TreatmentResponse create(
            TreatmentRequest request
    ) {

        Treatment treatment = Treatment.builder()
                .name(request.getName())
                .description(request.getDescription())
                .durationMinutes(request.getDurationMinutes())
                .price(request.getPrice())
                .build();

        treatmentRepository.save(treatment);

        return map(treatment);
    }

    public List<TreatmentResponse> getAll() {

        return treatmentRepository.findAll()
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    public TreatmentResponse getById(Long id) {

        Treatment treatment =
                treatmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Treatment not found"
                                )
                        );

        return map(treatment);
    }

    public TreatmentResponse update(
            Long id,
            TreatmentRequest request
    ) {

        Treatment treatment =
                treatmentRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Treatment not found"
                                )
                        );

        treatment.setName(request.getName());
        treatment.setDescription(request.getDescription());
        treatment.setDurationMinutes(
                request.getDurationMinutes()
        );
        treatment.setPrice(request.getPrice());

        treatmentRepository.save(treatment);

        return map(treatment);
    }

    public void delete(Long id) {

        if (!treatmentRepository.existsById(id)) {
            throw new RuntimeException(
                    "Treatment not found"
            );
        }

        treatmentRepository.deleteById(id);
    }

    private TreatmentResponse map(
            Treatment treatment
    ) {

        return TreatmentResponse.builder()
                .id(treatment.getId())
                .name(treatment.getName())
                .description(treatment.getDescription())
                .durationMinutes(
                        treatment.getDurationMinutes()
                )
                .price(treatment.getPrice())
                .build();
    }
}