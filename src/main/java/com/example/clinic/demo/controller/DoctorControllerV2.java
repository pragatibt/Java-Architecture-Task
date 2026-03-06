package com.example.clinic.demo.controller;
import com.example.clinic.demo.dto.DoctorResponseDTO;

import com.example.clinic.demo.entity.Doctor;
import com.example.clinic.demo.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/doctors")
@RequiredArgsConstructor
public class DoctorControllerV2 {

    private final DoctorService doctorService;

    // GET /api/v2/doctors?specialization=cardio&page=0&size=5
    @GetMapping
    public Page<DoctorResponseDTO> getDoctors(
            @RequestParam(required = false) String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Page<Doctor> doctors = doctorService.getAll(specialization, page, size);

        return doctors.map(this::convertToDTO);
    }

    // Convert Doctor entity to DTO
    private DoctorResponseDTO convertToDTO(Doctor doctor) {
        return DoctorResponseDTO.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .specialization(doctor.getSpecialization())
                .experienceYears(doctor.getExperienceYears())
                .build();
    }
}