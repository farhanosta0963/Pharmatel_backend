package com.pharmatel.backend.dto.patient;



import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDetailsRequest {

    @PastOrPresent
    private LocalDate dateOfBirth;

    private String gender; // MALE, FEMALE, OTHER, UNKNOWN

    private BigDecimal heightCm;

    private BigDecimal weightKg;

    private String diagnosis;

    private String allergies;

    // base64-encoded image, if submitting a photo via this endpoint
    private String imageBase64;
}