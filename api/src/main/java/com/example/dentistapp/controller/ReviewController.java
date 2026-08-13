package com.example.dentistapp.controller;

import com.example.dentistapp.dto.ReviewRequest;
import com.example.dentistapp.dto.ReviewResponse;
import com.example.dentistapp.service.ReviewService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @PostMapping
    public ReviewResponse create(
            @RequestBody ReviewRequest request
    ) {

        return reviewService.create(request);
    }


    @GetMapping("/dentist/{dentistId}")
    public List<ReviewResponse> getDentistReviews(
            @PathVariable Long dentistId
    ) {

        return reviewService.getDentistReviews(
                dentistId
        );
    }


    @GetMapping("/my")
    public List<ReviewResponse> getMyReviews() {

        return reviewService.getMyReviews();
    }


    @GetMapping("/dentist/{dentistId}/rating")
    public double getDentistAverageRating(
            @PathVariable Long dentistId
    ) {

        return reviewService.getDentistAverageRating(
                dentistId
        );
    }
}