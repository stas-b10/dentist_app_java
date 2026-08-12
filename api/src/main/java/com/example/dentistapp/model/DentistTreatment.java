package com.example.dentistapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "dentist_treatments",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"dentist_id", "treatment_id"}
        )
    }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DentistTreatment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "dentist_id", nullable = false)
    private Dentist dentist;

    @ManyToOne
    @JoinColumn(name = "treatment_id", nullable = false)
    private Treatment treatment;
}