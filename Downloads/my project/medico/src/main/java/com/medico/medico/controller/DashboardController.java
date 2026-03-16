package com.medico.medico.controller;

import com.medico.medico.dto.PasswordUpdateRequest;
import com.medico.medico.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;

    @GetMapping("/doctor/dashboard")
    public ResponseEntity<String> doctorDashboard(Authentication authentication) {
        return ResponseEntity.ok(userService.getDoctorDashboard(authentication.getName()));
    }

    @GetMapping("/patient/dashboard")
    public ResponseEntity<String> patientDashboard(Authentication authentication) {
        return ResponseEntity.ok(userService.getPatientDashboard(authentication.getName()));
    }

    @PostMapping("/password")
    public ResponseEntity<String> updatePassword(Authentication authentication,
            @Valid @RequestBody PasswordUpdateRequest request) {
        userService.updatePassword(authentication.getName(), request);
        return ResponseEntity.ok("Password updated successfully");
    }
}
