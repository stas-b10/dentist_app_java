package com.example.dentistapp.service;

import com.example.dentistapp.dto.ReviewRequest;
import com.example.dentistapp.dto.ReviewResponse;
import com.example.dentistapp.model.Appointment;
import com.example.dentistapp.model.Client;
import com.example.dentistapp.model.Review;
import com.example.dentistapp.repository.AppointmentRepository;
import com.example.dentistapp.repository.ClientRepository;
import com.example.dentistapp.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    private final AppointmentRepository appointmentRepository;

    private final ClientRepository clientRepository;


    public ReviewResponse create(
            ReviewRequest request
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();


        Client client =
                clientRepository
                        .findByUserEmail(email)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Client profile not found"
                                )
                        );


        Appointment appointment =
                appointmentRepository
                        .findById(request.getAppointmentId())
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Appointment not found"
                                )
                        );


        if (!appointment.getClient()
                .getId()
                .equals(client.getId())) {

            throw new RuntimeException(
                    "You cannot review this appointment"
            );
        }


        if (!"ACCEPTED".equals(
                appointment.getStatus()
        )) {

            throw new RuntimeException(
                    "Only accepted appointments can be reviewed"
            );
        }


        if (reviewRepository.existsByAppointmentId(
                appointment.getId()
        )) {

            throw new RuntimeException(
                    "This appointment has already been reviewed"
            );
        }


        if (request.getRating() == null
                || request.getRating() < 1
                || request.getRating() > 5) {

            throw new RuntimeException(
                    "Rating must be between 1 and 5"
            );
        }


        Review review =
                Review.builder()
                        .client(client)
                        .dentist(
                                appointment.getDentist()
                        )
                        .appointment(appointment)
                        .rating(request.getRating())
                        .comment(request.getComment())
                        .createdAt(LocalDateTime.now())
                        .build();


        reviewRepository.save(review);


        return map(review);
    }


    public List<ReviewResponse> getDentistReviews(
            Long dentistId
    ) {

        return reviewRepository
                .findByDentistIdOrderByCreatedAtDesc(
                        dentistId
                )
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    public List<ReviewResponse> getMyReviews() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email = authentication.getName();


        Client client =
                clientRepository
                        .findByUserEmail(email)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Client profile not found"
                                )
                        );


        return reviewRepository
                .findByClientIdOrderByCreatedAtDesc(
                        client.getId()
                )
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }


    public double getDentistAverageRating(
            Long dentistId
    ) {

        List<Review> reviews =
                reviewRepository
                        .findByDentistId(dentistId);


        if (reviews.isEmpty()) {
            return 0.0;
        }


        double total =
                reviews.stream()
                        .mapToInt(
                                Review::getRating
                        )
                        .sum();


        return total / reviews.size();
    }


    private ReviewResponse map(
            Review review
    ) {

        return ReviewResponse.builder()
                .id(review.getId())
                .clientId(
                        review.getClient().getId()
                )
                .dentistId(
                        review.getDentist().getId()
                )
                .appointmentId(
                        review.getAppointment().getId()
                )
                .rating(
                        review.getRating()
                )
                .comment(
                        review.getComment()
                )
                .createdAt(
                        review.getCreatedAt()
                )
                .build();
    }
}