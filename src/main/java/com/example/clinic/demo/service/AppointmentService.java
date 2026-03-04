package com.example.clinic.demo.service;

import com.example.clinic.demo.entity.*;
import com.example.clinic.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final DoctorRepository doctorRepo;

    public Appointment create(Long doctorId, Appointment appointment) {

        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        appointment.setDoctor(doctor);
        appointment.setStatus("BOOKED");

        return appointmentRepo.save(appointment);
    }

    public Page<Appointment> getAll(Long doctorId,
            String status,
            int page,
            int size) {

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("appointmentTime").descending());

        if (doctorId != null) {
            return appointmentRepo.findByDoctorId(doctorId, pageable);
        }

        if (status != null) {
            return appointmentRepo.findByStatus(status, pageable);
        }

        return appointmentRepo.findAll(pageable);
    }
}
