package com.medico.medico.service.impl;

import com.medico.medico.dto.PasswordUpdateRequest;
import com.medico.medico.model.Role;
import com.medico.medico.model.User;
import com.medico.medico.repository.UserRepository;
import com.medico.medico.service.UserService;
import com.medico.medico.util.PasswordComplexityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @PreAuthorize("hasRole('DOCTOR')")
    public String getDoctorDashboard(String username) {
        return "Welcome to the doctor dashboard, " + username + "!";
    }

    @Override
    @PreAuthorize("hasRole('PATIENT')")
    public String getPatientDashboard(String username) {
        return "Welcome to the patient dashboard, " + username + "!";
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void updatePassword(String username, PasswordUpdateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        PasswordComplexityValidator.validate(request.getNewPassword());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
