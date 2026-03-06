package com.example.clinic.demo.service;

import com.example.clinic.demo.entity.Doctor;
import com.example.clinic.demo.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository repository;

    public Doctor create(Doctor doctor) {
        return repository.save(doctor);
    }

    public Page<Doctor> getAll(String specialization, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("name"));

        if (specialization != null && !specialization.isEmpty()) {
            return repository.findBySpecializationContainingIgnoreCase(
                    specialization, pageable);
        }

        return repository.findAll(pageable);
    }
}