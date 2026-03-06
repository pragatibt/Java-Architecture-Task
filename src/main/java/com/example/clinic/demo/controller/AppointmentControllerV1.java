package com.example.clinic.demo.controller;

import com.example.clinic.demo.entity.Appointment;
import com.example.clinic.demo.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentControllerV1 {

    private final AppointmentService service;

    @PostMapping("/{doctorId}")
    public Appointment create(@PathVariable Long doctorId,
            @RequestBody Appointment appointment) {
        return service.create(doctorId, appointment);
    }

    @GetMapping
    public Page<Appointment> getAll(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return service.getAll(doctorId, status, page, size);
    }
}