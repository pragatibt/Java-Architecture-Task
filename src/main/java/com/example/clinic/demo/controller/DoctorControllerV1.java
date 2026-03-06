package com.example.clinic.demo.controller;

import com.example.clinic.demo.entity.Doctor;
import com.example.clinic.demo.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorControllerV1 {

    private final DoctorService service;

    @PostMapping
    public Doctor create(@RequestBody Doctor doctor) {
        return service.create(doctor);
    }

    @GetMapping
    public Page<Doctor> getAll(
            @RequestParam(required = false) String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return service.getAll(specialization, page, size);
    }
}
