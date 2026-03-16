package com.medico.medico.config;

import com.medico.medico.model.Role;
import com.medico.medico.model.User;
import com.medico.medico.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initializeUsers(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("doctor").isEmpty()) {
                userRepository.save(User.builder()
                        .username("doctor")
                        .password(passwordEncoder.encode("Doctor@123"))
                        .role(Role.DOCTOR)
                        .enabled(true)
                        .build());
            }
            if (userRepository.findByUsername("patient").isEmpty()) {
                userRepository.save(User.builder()
                        .username("patient")
                        .password(passwordEncoder.encode("Patient@123"))
                        .role(Role.PATIENT)
                        .enabled(true)
                        .build());
            }
        };
    }
}
