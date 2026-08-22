package com.pharmatel.backend.dto.patient;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.pharmatel.backend.entity.Patient.Gender;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PatientDto {
    Integer id;
    String name;
    String email;
    String phoneNumber;
    
    String medicalRecordNumber;
    LocalDate dateOfBirth;
    Gender gender;
    BigDecimal heightCm;
    BigDecimal weightKg;
    byte[] imageBase64;
    String diagnosis;
    String allergies;


}
