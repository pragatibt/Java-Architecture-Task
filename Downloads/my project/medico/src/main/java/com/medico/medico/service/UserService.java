package com.medico.medico.service;

import com.medico.medico.dto.PasswordUpdateRequest;

public interface UserService {

    String getDoctorDashboard(String username);

    String getPatientDashboard(String username);

    void updatePassword(String username, PasswordUpdateRequest request);
}
