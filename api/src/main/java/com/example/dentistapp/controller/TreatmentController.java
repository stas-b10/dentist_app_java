package com.example.dentistapp.controller;


import com.example.dentistapp.dto.TreatmentRequest;
import com.example.dentistapp.dto.TreatmentResponse;
import com.example.dentistapp.service.TreatmentService;


import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.*;


import java.util.List;



@RestController
@RequestMapping("/treatments")
@RequiredArgsConstructor
public class TreatmentController {


    private final TreatmentService treatmentService;



    @PostMapping
    public TreatmentResponse create(
            @RequestBody TreatmentRequest request
    ){

        return treatmentService.create(request);

    }




    @GetMapping
    public List<TreatmentResponse> getAll(){

        return treatmentService.getAll();

    }




    @GetMapping("/client/{id}")
    public List<TreatmentResponse> getByClient(
            @PathVariable Long id
    ){

        return treatmentService.getByClient(id);

    }




    @GetMapping("/dentist/{id}")
    public List<TreatmentResponse> getByDentist(
            @PathVariable Long id
    ){

        return treatmentService.getByDentist(id);

    }

}