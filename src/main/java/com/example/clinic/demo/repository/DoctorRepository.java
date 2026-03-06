package com.example.clinic.demo.repository;

import com.example.clinic.demo.entity.Doctor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    Page<Doctor> findBySpecializationContainingIgnoreCase(
            String specialization, Pageable pageable);
}
