
package com.example.clinic.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorResponseDTO {
    private Long id;
    private String name;
    private String specialization;
    private Integer experienceYears;
}